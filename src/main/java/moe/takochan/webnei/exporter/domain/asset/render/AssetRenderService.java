package moe.takochan.webnei.exporter.domain.asset.render;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import moe.takochan.webnei.exporter.WebneiExporterMod;
import moe.takochan.webnei.exporter.domain.asset.model.AssetRow;
import moe.takochan.webnei.exporter.domain.asset.render.client.AssetRenderDispatcher;

/**
 * 资产渲染调度器。
 *
 * <p>
 * 渲染分两段并行：GL 渲染（客户端线程，按帧时间片批量产出 {@link RenderedAsset}）与 PNG 编码 + 落盘
 * （后台 encoder 线程池）。两段通过有界队列衔接，队列满时对生产者形成背压。调用线程（导出后台线程）
 * 阻塞等待两段全部完成后返回 {@link AssetRow} 列表。
 */
public final class AssetRenderService {

    private static final long FRAME_BUDGET_NANOS = TimeUnit.MILLISECONDS.toNanos(8L);
    private static final int QUEUE_CAPACITY = 256;
    private static final long STALL_TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(60L);
    private static final long POLL_INTERVAL_MILLIS = 200L;
    private static final RenderedAsset POISON = RenderedAsset.png(null, null, null, null);

    private final List<IAssetRenderer> renderers;
    private final int encoderThreads;

    public AssetRenderService() {
        this(Arrays.<IAssetRenderer>asList(new ItemIconRenderer(), new FluidIconRenderer()));
    }

    public AssetRenderService(List<IAssetRenderer> renderers) {
        this(
            renderers,
            Math.max(
                2,
                Runtime.getRuntime()
                    .availableProcessors() - 1));
    }

    public AssetRenderService(List<IAssetRenderer> renderers, int encoderThreads) {
        this.renderers = renderers;
        this.encoderThreads = Math.max(1, encoderThreads);
    }

    public List<AssetRow> renderAll(List<AssetRenderJob> jobs, File outputDirectory) {
        if (jobs.isEmpty()) {
            return Collections.emptyList();
        }

        BlockingQueue<RenderedAsset> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        List<AssetRow> rows = Collections.synchronizedList(new ArrayList<AssetRow>(jobs.size()));
        CountDownLatch producerDone = new CountDownLatch(1);
        AtomicReference<RuntimeException> producerError = new AtomicReference<>();
        AtomicLong progress = new AtomicLong(System.nanoTime());

        ExecutorService encoders = Executors.newFixedThreadPool(encoderThreads, encoderThreadFactory());
        List<CountDownLatch> encoderDone = startEncoders(encoders, queue, rows, outputDirectory);

        Producer producer = new Producer(jobs, outputDirectory, queue, producerDone, producerError, progress);
        AssetRenderDispatcher.INSTANCE.setFrameTask(producer);

        try {
            awaitProducer(producerDone, progress);
            enqueuePoison(queue);
            awaitEncoders(encoderDone);
        } finally {
            encoders.shutdownNow();
        }

        RuntimeException error = producerError.get();
        if (error != null) {
            WebneiExporterMod.LOG.error("Asset render producer failed", error);
        }
        return new ArrayList<>(rows);
    }

    private IAssetRenderer rendererFor(AssetRenderJob job) {
        for (IAssetRenderer renderer : renderers) {
            if (renderer.supports(job)) {
                return renderer;
            }
        }
        return null;
    }

    private void awaitProducer(CountDownLatch producerDone, AtomicLong progress) {
        while (true) {
            try {
                if (producerDone.await(POLL_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)) {
                    return;
                }
            } catch (InterruptedException e) {
                Thread.currentThread()
                    .interrupt();
                AssetRenderDispatcher.INSTANCE.setFrameTask(null);
                return;
            }
            if (System.nanoTime() - progress.get() > STALL_TIMEOUT_NANOS) {
                WebneiExporterMod.LOG.error("Asset render stalled; no render tick progress within timeout");
                AssetRenderDispatcher.INSTANCE.setFrameTask(null);
                return;
            }
        }
    }

    private void enqueuePoison(BlockingQueue<RenderedAsset> queue) {
        for (int i = 0; i < encoderThreads; i++) {
            putUninterruptibly(queue, POISON);
        }
    }

    private void awaitEncoders(List<CountDownLatch> encoderDone) {
        for (CountDownLatch latch : encoderDone) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread()
                    .interrupt();
                return;
            }
        }
    }

    private List<CountDownLatch> startEncoders(ExecutorService encoders, BlockingQueue<RenderedAsset> queue,
        List<AssetRow> rows, File outputDirectory) {
        List<CountDownLatch> latches = new ArrayList<>(encoderThreads);
        for (int i = 0; i < encoderThreads; i++) {
            CountDownLatch done = new CountDownLatch(1);
            latches.add(done);
            encoders.execute(new Encoder(queue, rows, outputDirectory, done));
        }
        return latches;
    }

    private static void putUninterruptibly(BlockingQueue<RenderedAsset> queue, RenderedAsset asset) {
        boolean interrupted = false;
        while (true) {
            try {
                queue.put(asset);
                break;
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
        if (interrupted) {
            Thread.currentThread()
                .interrupt();
        }
    }

    private static ThreadFactory encoderThreadFactory() {
        final AtomicInteger counter = new AtomicInteger(1);
        return new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "WebNEI Asset Encoder " + counter.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        };
    }

    private static AssetRow toRow(RenderedAsset asset) {
        AssetRenderJob job = asset.getJob();
        return new AssetRow(
            job.getDatasetId(),
            job.getOwnerType(),
            job.getOwnerId(),
            job.getKind(),
            asset.getRelativePath(),
            asset.getMediaType(),
            asset.getImage()
                .getWidth(),
            asset.getImage()
                .getHeight(),
            asset.getMetadataJson());
    }

    /** 客户端线程帧任务：按时间片渲染图标并入队，队列满则当帧让出、下帧续渲。 */
    private final class Producer implements AssetRenderDispatcher.FrameTask {

        private final List<AssetRenderJob> jobs;
        private final File outputDirectory;
        private final BlockingQueue<RenderedAsset> queue;
        private final CountDownLatch done;
        private final AtomicReference<RuntimeException> error;
        private final AtomicLong progress;
        private int index;
        private RenderedAsset pending;

        private Producer(List<AssetRenderJob> jobs, File outputDirectory, BlockingQueue<RenderedAsset> queue,
            CountDownLatch done, AtomicReference<RuntimeException> error, AtomicLong progress) {
            this.jobs = jobs;
            this.outputDirectory = outputDirectory;
            this.queue = queue;
            this.done = done;
            this.error = error;
            this.progress = progress;
        }

        @Override
        public boolean runSlice() {
            long deadline = System.nanoTime() + FRAME_BUDGET_NANOS;
            progress.set(System.nanoTime());

            if (pending != null) {
                if (!queue.offer(pending)) {
                    return true;
                }
                pending = null;
            }

            while (index < jobs.size()) {
                if (System.nanoTime() >= deadline) {
                    return true;
                }
                AssetRenderJob job = jobs.get(index);
                index++;
                RenderedAsset asset = render(job);
                if (asset == null) {
                    continue;
                }
                asset.setOutputFile(new File(outputDirectory, asset.getRelativePath()));
                if (!queue.offer(asset)) {
                    pending = asset;
                    return true;
                }
            }
            done.countDown();
            return false;
        }

        @Override
        public void onFailed(RuntimeException e) {
            error.compareAndSet(null, e);
            done.countDown();
        }

        private RenderedAsset render(AssetRenderJob job) {
            IAssetRenderer renderer = rendererFor(job);
            if (renderer == null) {
                WebneiExporterMod.LOG.warn(
                    "No asset renderer for ownerType={}, ownerId={}, kind={}",
                    job.getOwnerType(),
                    job.getOwnerId(),
                    job.getKind());
                return null;
            }
            try {
                return renderer.renderImage(job);
            } catch (AssetRenderException | RuntimeException e) {
                WebneiExporterMod.LOG.warn(
                    "Failed to render asset: ownerType={}, ownerId={}, kind={}",
                    job.getOwnerType(),
                    job.getOwnerId(),
                    job.getKind(),
                    e);
                return null;
            }
        }
    }

    /** 后台 encoder：从队列取 {@link RenderedAsset}，编码 PNG 落盘并产出 {@link AssetRow}。 */
    private static final class Encoder implements Runnable {

        private final BlockingQueue<RenderedAsset> queue;
        private final List<AssetRow> rows;
        private final File outputDirectory;
        private final CountDownLatch done;

        private Encoder(BlockingQueue<RenderedAsset> queue, List<AssetRow> rows, File outputDirectory,
            CountDownLatch done) {
            this.queue = queue;
            this.rows = rows;
            this.outputDirectory = outputDirectory;
            this.done = done;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    RenderedAsset asset = queue.take();
                    if (asset == POISON) {
                        return;
                    }
                    encode(asset);
                }
            } catch (InterruptedException e) {
                Thread.currentThread()
                    .interrupt();
            } finally {
                done.countDown();
            }
        }

        private void encode(RenderedAsset asset) {
            File outputFile = asset.getOutputFile();
            if (outputFile == null) {
                outputFile = new File(outputDirectory, asset.getRelativePath());
            }
            try {
                PngAssetFile.write(asset.getImage(), outputFile);
                rows.add(toRow(asset));
            } catch (AssetRenderException | RuntimeException e) {
                AssetRenderJob job = asset.getJob();
                WebneiExporterMod.LOG.warn(
                    "Failed to encode asset: ownerType={}, ownerId={}, kind={}",
                    job.getOwnerType(),
                    job.getOwnerId(),
                    job.getKind(),
                    e);
            }
        }
    }
}
