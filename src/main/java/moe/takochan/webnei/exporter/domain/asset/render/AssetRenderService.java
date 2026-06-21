package moe.takochan.webnei.exporter.domain.asset.render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import moe.takochan.webnei.exporter.domain.asset.render.client.FboIconRenderer;

/**
 * 资产渲染调度器。
 *
 * <p>
 * 渲染分两段并行：GL 渲染（客户端线程，按帧时间片产出 {@link RenderedAsset}）与 PNG 编码 + 落盘
 * （后台 encoder 线程池）。GL 段把同尺寸的静态图标攒成一批，经 atlas 一次 readback 渲出（见
 * {@link FboIconRenderer#renderBatch}）；动画等不可批量的图标退回逐个渲染。两段通过有界队列衔接，
 * 队列满时对生产者形成背压。调用线程（导出后台线程）阻塞等待两段全部完成后返回 {@link AssetRow} 列表。
 */
public final class AssetRenderService {

    private static final long FRAME_BUDGET_NANOS = TimeUnit.MILLISECONDS.toNanos(8L);
    private static final int QUEUE_CAPACITY = 256;
    /** 资产 zip 文件名，落在 bundle 输出目录下，由部署侧解压。 */
    private static final String ASSET_ZIP_NAME = "assets.zip";
    /** 单次 atlas flush 的图标数上限，限制单帧 GL 工作量。 */
    private static final int MAX_BATCH = 64;
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
        return renderAll(jobs, outputDirectory, AssetRenderProgress.NONE);
    }

    public List<AssetRow> renderAll(List<AssetRenderJob> jobs, File outputDirectory, AssetRenderProgress progress) {
        if (jobs.isEmpty()) {
            return Collections.emptyList();
        }

        File zipFile = new File(outputDirectory, ASSET_ZIP_NAME);
        ZipAssetWriter zipWriter;
        try {
            zipWriter = ZipAssetWriter.create(zipFile);
        } catch (IOException e) {
            WebneiExporterMod.LOG.error("Unable to create asset zip: {}", zipFile.getAbsolutePath(), e);
            return Collections.emptyList();
        }

        BlockingQueue<RenderedAsset> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        List<AssetRow> rows = Collections.synchronizedList(new ArrayList<AssetRow>(jobs.size()));
        CountDownLatch producerDone = new CountDownLatch(1);
        AtomicReference<RuntimeException> producerError = new AtomicReference<>();
        AtomicLong tickProgress = new AtomicLong(System.nanoTime());

        ExecutorService encoders = Executors.newFixedThreadPool(encoderThreads, encoderThreadFactory());
        List<CountDownLatch> encoderDone = startEncoders(encoders, queue, rows, zipWriter);

        Producer producer = new Producer(jobs, queue, producerDone, producerError, tickProgress, progress);
        AssetRenderDispatcher.INSTANCE.setFrameTask(producer);

        try {
            awaitProducer(producerDone, tickProgress);
            enqueuePoison(queue);
            awaitEncoders(encoderDone);
        } finally {
            encoders.shutdownNow();
            closeQuietly(zipWriter, zipFile);
        }

        RuntimeException error = producerError.get();
        if (error != null) {
            WebneiExporterMod.LOG.error("Asset render producer failed", error);
        }
        return new ArrayList<>(rows);
    }

    private static void closeQuietly(ZipAssetWriter zipWriter, File zipFile) {
        try {
            zipWriter.close();
        } catch (IOException e) {
            WebneiExporterMod.LOG.error("Unable to finalize asset zip: {}", zipFile.getAbsolutePath(), e);
        }
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
        List<AssetRow> rows, ZipAssetWriter zipWriter) {
        List<CountDownLatch> latches = new ArrayList<>(encoderThreads);
        for (int i = 0; i < encoderThreads; i++) {
            CountDownLatch done = new CountDownLatch(1);
            latches.add(done);
            encoders.execute(new Encoder(queue, rows, zipWriter, done));
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

    /**
     * 客户端线程帧任务：把同尺寸静态图标攒成批，按 atlas 容量或时间片 flush；动画等退回逐个渲染。
     *
     * <p>
     * 单帧 GL 工作量受 {@link #FRAME_BUDGET_NANOS} 与 {@link #MAX_BATCH} 双重限制。队列满时产物存入
     * {@code overflow}，下一帧优先回灌，形成对生产者的背压。{@code atlas} 仅在客户端线程访问，无需同步。
     */
    private final class Producer implements AssetRenderDispatcher.FrameTask {

        private final List<AssetRenderJob> jobs;
        private final BlockingQueue<RenderedAsset> queue;
        private final CountDownLatch done;
        private final AtomicReference<RuntimeException> error;
        private final AtomicLong tickProgress;
        private final AssetRenderProgress progress;
        private final FboIconRenderer atlas = new FboIconRenderer();
        private final Map<Integer, List<IconTile>> buckets = new LinkedHashMap<>();
        private final Deque<RenderedAsset> overflow = new ArrayDeque<>();
        private int index;
        private int finished;

        private Producer(List<AssetRenderJob> jobs, BlockingQueue<RenderedAsset> queue, CountDownLatch done,
            AtomicReference<RuntimeException> error, AtomicLong tickProgress, AssetRenderProgress progress) {
            this.jobs = jobs;
            this.queue = queue;
            this.done = done;
            this.error = error;
            this.tickProgress = tickProgress;
            this.progress = progress;
        }

        @Override
        public boolean runSlice() {
            long deadline = System.nanoTime() + FRAME_BUDGET_NANOS;
            tickProgress.set(System.nanoTime());

            if (!drainOverflow()) {
                return true;
            }

            while (index < jobs.size()) {
                if (System.nanoTime() >= deadline) {
                    report();
                    return true;
                }
                processJob(jobs.get(index));
                index++;
                if (!drainOverflow()) {
                    report();
                    return true;
                }
            }

            flushAllBuckets();
            if (!drainOverflow()) {
                report();
                return true;
            }
            report();
            done.countDown();
            return false;
        }

        private void report() {
            progress.onProgress(finished, jobs.size());
        }

        @Override
        public void onFailed(RuntimeException e) {
            error.compareAndSet(null, e);
            done.countDown();
        }

        private void processJob(AssetRenderJob job) {
            IAssetRenderer renderer = rendererFor(job);
            if (renderer == null) {
                WebneiExporterMod.LOG.warn(
                    "No asset renderer for ownerType={}, ownerId={}, kind={}",
                    job.getOwnerType(),
                    job.getOwnerId(),
                    job.getKind());
                finished++;
                return;
            }
            IconTile tile;
            try {
                tile = renderer.prepareTile(job);
            } catch (AssetRenderException | RuntimeException e) {
                warnFailed(job, e);
                finished++;
                return;
            }
            if (tile == null) {
                renderDirect(renderer, job);
                finished++;
                return;
            }
            bucket(tile);
        }

        private void bucket(IconTile tile) {
            List<IconTile> bucket = buckets.computeIfAbsent(tile.getSize(), k -> new ArrayList<>());
            bucket.add(tile);
            if (bucket.size() >= Math.min(MAX_BATCH, atlas.batchCapacity(tile.getSize()))) {
                flushBucket(tile.getSize(), bucket);
                buckets.remove(tile.getSize());
            }
        }

        private void flushAllBuckets() {
            for (Map.Entry<Integer, List<IconTile>> entry : buckets.entrySet()) {
                flushBucket(entry.getKey(), entry.getValue());
            }
            buckets.clear();
        }

        private void flushBucket(int size, List<IconTile> tiles) {
            if (tiles.isEmpty()) {
                return;
            }
            List<FboIconRenderer.IconRenderAction> actions = new ArrayList<>(tiles.size());
            for (IconTile tile : tiles) {
                actions.add(tile.getAction());
            }
            try {
                List<BufferedImage> images = atlas.renderBatch(size, actions);
                for (int i = 0; i < tiles.size(); i++) {
                    IconTile tile = tiles.get(i);
                    emit(
                        RenderedAsset
                            .png(tile.getJob(), tile.getRelativePath(), images.get(i), tile.getMetadataJson()));
                    finished++;
                }
            } catch (AssetRenderException | RuntimeException e) {
                // 批次失败可能源于其中某个图标；逐个重渲以隔离坏图标，避免整批丢失。
                for (IconTile tile : tiles) {
                    renderTileIndividually(tile);
                    finished++;
                }
            }
        }

        private void renderTileIndividually(IconTile tile) {
            try {
                BufferedImage image = atlas.render(tile.getSize(), tile.getAction());
                emit(RenderedAsset.png(tile.getJob(), tile.getRelativePath(), image, tile.getMetadataJson()));
            } catch (AssetRenderException | RuntimeException e) {
                warnFailed(tile.getJob(), e);
            }
        }

        private void renderDirect(IAssetRenderer renderer, AssetRenderJob job) {
            try {
                emit(renderer.renderImage(job));
            } catch (AssetRenderException | RuntimeException e) {
                warnFailed(job, e);
            }
        }

        private void emit(RenderedAsset asset) {
            if (!queue.offer(asset)) {
                overflow.addLast(asset);
            }
        }

        /** 回灌上一帧积压的产物；队列仍满时返回 false，让出本帧。 */
        private boolean drainOverflow() {
            while (!overflow.isEmpty()) {
                if (!queue.offer(overflow.peekFirst())) {
                    return false;
                }
                overflow.removeFirst();
            }
            return true;
        }

        private void warnFailed(AssetRenderJob job, Exception e) {
            WebneiExporterMod.LOG.warn(
                "Failed to render asset: ownerType={}, ownerId={}, kind={}",
                job.getOwnerType(),
                job.getOwnerId(),
                job.getKind(),
                e);
        }
    }

    /** 后台 encoder：从队列取 {@link RenderedAsset}，并行编码 PNG 字节并追加进 zip，产出 {@link AssetRow}。 */
    private static final class Encoder implements Runnable {

        private final BlockingQueue<RenderedAsset> queue;
        private final List<AssetRow> rows;
        private final ZipAssetWriter zipWriter;
        private final CountDownLatch done;

        private Encoder(BlockingQueue<RenderedAsset> queue, List<AssetRow> rows, ZipAssetWriter zipWriter,
            CountDownLatch done) {
            this.queue = queue;
            this.rows = rows;
            this.zipWriter = zipWriter;
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
            try {
                // 并行：编码 PNG 字节并算好 STORED entry；串行：仅追加进 zip（临界区极短）。
                byte[] data = PngAssetFile.encode(asset.getImage());
                zipWriter.writeEntry(ZipAssetWriter.storedEntry(asset.getRelativePath(), data), data);
                rows.add(toRow(asset));
            } catch (AssetRenderException | IOException | RuntimeException e) {
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
