package moe.takochan.webnei.exporter.domain.asset.render.hook;

/**
 * 通过 {@link WebneiTimeSource} 的伪造时钟驱动 wallclock 渲染器的会话。每一步推进 {@code stepMillis}，
 * 经 mixin 改写后的渲染器在采样过程中会读到递增的虚拟毫秒值。
 *
 * <p>
 * baseline 取 {@code System.currentTimeMillis()} 当前值，{@link #close} 时直接 deactivate（不是改回
 * baseline——透传机制本就回到真实时钟，无需还原）。
 */
public final class WallclockSession implements ITimeDriverSession {

    private final long baseline;
    private final int stepMillis;

    /**
     * @param stepMillis 每帧推进的毫秒数
     */
    public WallclockSession(int stepMillis) {
        this.baseline = System.currentTimeMillis();
        this.stepMillis = stepMillis;
        WebneiTimeSource.activate(baseline);
    }

    @Override
    public void advanceTo(int step) {
        WebneiTimeSource.advance(baseline + (long) step * stepMillis);
    }

    @Override
    public void close() {
        WebneiTimeSource.deactivate();
    }
}
