package moe.takochan.webnei.exporter.domain.asset.render.hook;

/**
 * 时间驱动 hook 的可插拔时钟源。默认透传到真实系统时钟；当 hook 进入采样会话时，
 * 调用 {@link #activate} 切换到伪造时间，{@link #deactivate} 还原。
 *
 * <p>
 * 由 mixin 改写目标渲染器内部 {@code System.currentTimeMillis() / Sys.getTime() /
 * Minecraft.getSystemTime()} 的调用点为对此处的调用，从而让导出器在采样窗口内驱动它们。
 *
 * <p>
 * 状态用 {@link ThreadLocal} 隔离——采样跑在客户端主线程，其他线程不受影响。
 */
public final class WebneiTimeSource {

    /** {@code null} 表示透传；非 null 持有当前伪造毫秒时间。 */
    private static final ThreadLocal<long[]> OVERRIDE_MILLIS = new ThreadLocal<>();

    private WebneiTimeSource() {}

    // ---------- mixin 改写后调用的方法 ----------

    /** mixin 把 {@code System.currentTimeMillis()} 重定向到此处。 */
    public static long currentTimeMillis() {
        long[] override = OVERRIDE_MILLIS.get();
        return override != null ? override[0] : System.currentTimeMillis();
    }

    /**
     * mixin 把 {@code Minecraft.getSystemTime()} 重定向到此处。
     * vanilla 实现是 {@code Sys.getTime() * 1000 / Sys.getTimerResolution()}，等价于毫秒，
     * 因此和 {@link #currentTimeMillis()} 共用伪造源。
     */
    public static long minecraftSystemTime() {
        long[] override = OVERRIDE_MILLIS.get();
        return override != null ? override[0] : net.minecraft.client.Minecraft.getSystemTime();
    }

    /**
     * mixin 把 {@code org.lwjgl.Sys.getTime()} 重定向到此处。Sys.getTime() 单位是 timer ticks
     * （配合 {@code Sys.getTimerResolution()} 转毫秒），但渲染器使用模式都是 {@code Sys.getTime() / N}
     * 取角度/相位——伪造时只要保持 timer ticks/毫秒线性关系即可，因此返回伪造毫秒直接当 timer ticks 使用。
     */
    public static long sysGetTime() {
        long[] override = OVERRIDE_MILLIS.get();
        return override != null ? override[0] : org.lwjgl.Sys.getTime();
    }

    // ---------- hook 调用的会话控制 ----------

    /** 进入伪造时间会话，所有上述方法返回 {@code fakeMillis}。 */
    public static void activate(long fakeMillis) {
        OVERRIDE_MILLIS.set(new long[] { fakeMillis });
    }

    /** 推进到新的伪造毫秒值（采样循环每帧调一次）。 */
    public static void advance(long fakeMillis) {
        long[] override = OVERRIDE_MILLIS.get();
        if (override != null) {
            override[0] = fakeMillis;
        }
    }

    /** 退出会话，后续调用恢复透传到真实系统时钟。 */
    public static void deactivate() {
        OVERRIDE_MILLIS.remove();
    }
}
