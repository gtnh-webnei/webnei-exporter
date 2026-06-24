package moe.takochan.webnei.exporter.domain.asset.render.hook;

/**
 * 时间驱动多帧采样的一次会话。{@link #close()} 必须把时间状态复原到 {@link ITimeDriverHook#begin()} 时的基线，
 * 避免泄漏到后续渲染。
 */
public interface ITimeDriverSession extends AutoCloseable {

    /** 把时间状态推到 {@code baseline + step}。 */
    void advanceTo(int step);

    @Override
    void close();
}
