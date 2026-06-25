package moe.takochan.webnei.exporter.domain.asset.render.hook;

/**
 * 通过 {@link WebneiTimeSource} 的伪造时钟驱动 wallclock 渲染器的会话。每一步推进 {@code stepMillis}，
 * 经 mixin 改写后的渲染器在采样过程中会读到递增的虚拟毫秒值。
 *
 * <p>
 * baseline 固定从 {@code 0} 开始，而非真实 {@code System.currentTimeMillis()}。渲染器旋转公式形如
 * {@code time / N % 360}，对周期动画起点偏移只影响初始相位，不影响逐帧增量。更关键的是：部分渲染器（如
 * Galacticraft 火箭 {@code Sys.getTime() / 30F % 360F}）先把 {@code long} 转成 {@code float} 再做除法，
 * 而 {@code float} 仅 24 位尾数——万亿级的真实时间戳下，最小可分辨间隔远大于每帧增量，60 帧会被精度
 * 量化成同一个值导致动画静止。从 0 起步使采样值保持在小量级，规避该精度丢失。
 *
 * <p>
 * {@link #close} 时直接 deactivate（不是改回 baseline——透传机制本就回到真实时钟，无需还原）。
 */
public final class WallclockSession implements ITimeDriverSession {

    private static final long BASELINE = 0L;

    private final int stepMillis;

    /**
     * @param stepMillis 每帧推进的毫秒数
     */
    public WallclockSession(int stepMillis) {
        this.stepMillis = stepMillis;
        WebneiTimeSource.activate(BASELINE);
    }

    @Override
    public void advanceTo(int step) {
        WebneiTimeSource.advance(BASELINE + (long) step * stepMillis);
    }

    @Override
    public void close() {
        WebneiTimeSource.deactivate();
    }
}
