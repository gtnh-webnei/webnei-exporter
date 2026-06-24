package moe.takochan.webnei.exporter.domain.asset.render.hook;

import java.util.List;

import moe.takochan.webnei.exporter.domain.asset.render.AssetRenderJob;
import moe.takochan.webnei.exporter.engine.hook.HookRegistry;

/** 持有所有可用的时间驱动 hook，由 {@code ItemIconRenderer} / {@code FluidIconRenderer} 在渲染路径决策时查询。 */
public final class TimeDriverHookRegistry {

    private final List<ITimeDriverHook> hooks;

    /**
     * 创建 time driver hook registry。
     */
    public TimeDriverHookRegistry() {
        this.hooks = HookRegistry.get(ITimeDriverHook.class);
    }

    /**
     * 找到覆盖该资产的第一个 hook；都不命中返回 null（资产不走时间驱动采样）。
     *
     * @param job 渲染任务
     * @return 命中的 hook 或 null
     */
    public ITimeDriverHook find(AssetRenderJob job) {
        for (ITimeDriverHook hook : hooks) {
            if (hook.applies(job)) {
                return hook;
            }
        }
        return null;
    }
}
