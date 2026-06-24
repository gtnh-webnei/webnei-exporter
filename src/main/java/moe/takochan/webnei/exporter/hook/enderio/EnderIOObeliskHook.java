package moe.takochan.webnei.exporter.hook.enderio;

import java.lang.reflect.Field;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.common.Loader;
import crazypants.enderio.CommonProxy;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.obelisk.ObeliskSpecialRenderer;
import crazypants.enderio.machine.obelisk.aversion.AversionObeliskRenderer;
import crazypants.enderio.machine.obelisk.weather.WeatherObeliskSpecialRenderer;
import moe.takochan.webnei.exporter.domain.asset.render.hook.AbstractPlayerTickHook;
import moe.takochan.webnei.exporter.domain.asset.render.hook.ITimeDriverSession;

/**
 * EnderIO 四类 obelisk 方块物品（Attractor / Aversion / Experience / Weather），共用
 * {@link ObeliskSpecialRenderer} 及其子类。INVENTORY 渲染走 {@link ObeliskSpecialRenderer#renderItem}，
 * 内部 {@code renderItemStack} 把 {@code EnderIO.proxy.getTickCount() * 0.05f} 写入
 * {@code EntityItem.hoverStart}，由 vanilla RenderItem 在 Y 轴做 sin 浮动（真 GL 平移，非 lightmap，
 * GUI 路径有效）。
 *
 * <p>
 * 时间源 {@code CommonProxy.clientTickCount} 是 {@code protected long}，无 setter；
 * 通过反射写该字段，会话结束时复原。
 *
 * <p>
 * hoverStart 周期 = {@code 2π / 0.05 ≈ 125.66 tick}，覆盖 60 帧（默认）只有 47.6% 周期，回放会跳；
 * override {@link #sampleCount} 返回 126 让 spritesheet 完整闭环。
 */
public final class EnderIOObeliskHook extends AbstractPlayerTickHook {

    /** hoverStart 一个完整 sin 周期约 126 tick；让 spritesheet 闭环。 */
    private static final int FULL_HOVER_PERIOD = 126;

    private static final Field CLIENT_TICK_COUNT = lookupClientTickCount();

    @Override
    public int sampleCount() {
        return FULL_HOVER_PERIOD;
    }

    @Override
    public boolean isAvailable() {
        return Loader.isModLoaded(EnderIO.MODID) && CLIENT_TICK_COUNT != null;
    }

    @Override
    protected boolean matchesItem(ItemStack stack) {
        IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.INVENTORY);
        return renderer instanceof ObeliskSpecialRenderer || renderer instanceof AversionObeliskRenderer
            || renderer instanceof WeatherObeliskSpecialRenderer;
    }

    @Override
    public ITimeDriverSession begin() {
        try {
            long baseline = CLIENT_TICK_COUNT.getLong(EnderIO.proxy);
            return new ProxyTickCountSession(baseline);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /** 推进 {@code CommonProxy.clientTickCount}（反射写 protected 字段）；close 时复原。 */
    private static final class ProxyTickCountSession implements ITimeDriverSession {

        private final long baseline;

        ProxyTickCountSession(long baseline) {
            this.baseline = baseline;
        }

        @Override
        public void advanceTo(int step) {
            try {
                CLIENT_TICK_COUNT.setLong(EnderIO.proxy, baseline + step);
            } catch (IllegalAccessException ignored) {
                // 字段在 isAvailable 阶段已通过反射可达性校验，运行时不应再失败；忽略。
            }
        }

        @Override
        public void close() {
            try {
                CLIENT_TICK_COUNT.setLong(EnderIO.proxy, baseline);
            } catch (IllegalAccessException ignored) {
                // 同 advanceTo。
            }
        }
    }

    private static Field lookupClientTickCount() {
        try {
            Field field = CommonProxy.class.getDeclaredField("clientTickCount");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
