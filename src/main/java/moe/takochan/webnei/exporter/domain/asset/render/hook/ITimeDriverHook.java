package moe.takochan.webnei.exporter.domain.asset.render.hook;

import moe.takochan.webnei.exporter.domain.asset.render.AssetRenderJob;
import moe.takochan.webnei.exporter.engine.hook.IExportHook;

/**
 * 渲染驱动动画的时间源策略 hook。每个实现描述一类自定义渲染器读取时间状态的方式（player tick / world time /
 * wallclock 等），并在采样窗口内按步推进时间，使依赖该状态的渲染逻辑产出逐帧不同的输出。
 *
 * <p>
 * 路径决策由 {@link TimeDriverHookRegistry#find(AssetRenderJob)} 集中处理：返回非 null 的 hook 即代表该
 * 资产需要走时间驱动多帧采样。{@link IExportHook#isAvailable} 用于在目标 mod 未加载的环境下跳过 hook，
 * 由 {@link moe.takochan.webnei.exporter.engine.hook.HookRegistry#init} 阶段调用。
 */
public interface ITimeDriverHook extends IExportHook {

    /** 该 hook 是否覆盖给定资产。物品/流体的判断细节由实现自行处理。 */
    boolean applies(AssetRenderJob job);

    /** 采样窗口长度（帧数）。 */
    int sampleCount();

    /** 每帧推进的时间步长（单位由实现自定义，例如 player tick 数、毫秒等）。 */
    int tickStep();

    /**
     * 进入采样会话：以当前时间状态作为基线，由会话内部记录；若运行时上下文不可用（如 player==null），返回 null
     * 让调用方退化为单帧静态。
     */
    ITimeDriverSession begin();
}
