package moe.takochan.webnei.exporter.domain.asset.render.client;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.minecraft.client.Minecraft;

import moe.takochan.webnei.exporter.domain.asset.render.AssetRenderException;

public enum AssetRenderDispatcher {

    INSTANCE;

    private static final long RENDER_TIMEOUT_SECONDS = 60L;

    private final Queue<FutureTask<?>> tasks = new ConcurrentLinkedQueue<>();

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

    void drain() {
        FutureTask<?> task;
        while ((task = tasks.poll()) != null) {
            task.run();
        }
    }
}
