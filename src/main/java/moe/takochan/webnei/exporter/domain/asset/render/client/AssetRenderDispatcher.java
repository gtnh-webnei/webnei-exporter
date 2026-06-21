package moe.takochan.webnei.exporter.domain.asset.render.client;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.client.Minecraft;

import moe.takochan.webnei.exporter.domain.asset.render.AssetRenderException;

/**
 * 客户端线程 GL 工作泵。
 *
 * <p>
 * 每个 render tick 末尾，{@link AssetRenderTickHandler} 调用 {@link #drain()}。drain 先执行一次性
 * 提交的 callable（保留旧用途），再驱动一次已注册的帧任务 {@link FrameTask}。帧任务在客户端线程上
 * 自行做时间片，单帧渲染一批图标，从而把吞吐从“每帧一个图标”提升到“每帧一批图标”。
 */
public enum AssetRenderDispatcher {

    INSTANCE;

    private static final long RENDER_TIMEOUT_SECONDS = 60L;

    private final Queue<FutureTask<?>> tasks = new ConcurrentLinkedQueue<>();
    private final AtomicReference<FrameTask> frameTask = new AtomicReference<>();

    /**
     * 在客户端线程执行一次性 callable。若当前已在客户端线程则同步执行，否则入队等待下一个 render tick。
     */
    public <T> T call(Callable<T> callable) throws AssetRenderException {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.func_152345_ab()) {
            try {
                return callable.call();
            } catch (AssetRenderException e) {
                throw e;
            } catch (Exception e) {
                throw new AssetRenderException("Unable to render asset on client thread", e);
            }
        }

        FutureTask<T> task = new FutureTask<>(callable);
        tasks.add(task);
        try {
            return task.get(RENDER_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread()
                .interrupt();
            throw new AssetRenderException("Interrupted while waiting for asset render", e);
        } catch (TimeoutException e) {
            tasks.remove(task);
            throw new AssetRenderException("Timed out while waiting for asset render tick", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AssetRenderException) {
                throw (AssetRenderException) cause;
            }
            throw new AssetRenderException("Unable to render asset on client thread", cause);
        }
    }

    /**
     * 注册每帧驱动的渲染任务。每个 render tick 调用一次 {@link FrameTask#runSlice()}，返回 true 表示
     * 还有剩余工作、下一帧继续；返回 false 表示完成并自动注销。
     */
    public void setFrameTask(FrameTask task) {
        frameTask.set(task);
    }

    /** 非阻塞地把一个 Runnable 排入客户端线程，在下一个 render tick 执行。 */
    public void runLater(Runnable runnable) {
        tasks.add(new FutureTask<>(runnable, null));
    }

    void drain() {
        FutureTask<?> task;
        while ((task = tasks.poll()) != null) {
            task.run();
        }
        FrameTask current = frameTask.get();
        if (current == null) {
            return;
        }
        boolean more;
        try {
            more = current.runSlice();
        } catch (RuntimeException e) {
            frameTask.compareAndSet(current, null);
            current.onFailed(e);
            return;
        }
        if (!more) {
            frameTask.compareAndSet(current, null);
        }
    }

    /** 客户端线程每帧驱动的渲染任务。 */
    public interface FrameTask {

        /** 执行一帧时间片的渲染。返回 true 表示还有剩余工作。 */
        boolean runSlice();

        /** 帧任务抛出未捕获异常时回调，用于唤醒等待方。 */
        void onFailed(RuntimeException error);
    }
}
