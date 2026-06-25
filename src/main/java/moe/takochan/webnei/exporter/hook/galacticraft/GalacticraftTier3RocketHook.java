package moe.takochan.webnei.exporter.hook.galacticraft;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import micdoodle8.mods.galacticraft.planets.asteroids.client.render.item.ItemRendererTier3Rocket;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.asset.render.hook.AbstractPlayerTickHook;
import moe.takochan.webnei.exporter.domain.asset.render.hook.ITimeDriverSession;
import moe.takochan.webnei.exporter.domain.asset.render.hook.WallclockSession;

/**
 * Galacticraft {@code ItemRendererTier3Rocket}（GalacticraftMars 子模块 asteroids）：{@code transform} 内读
 * {@code Sys.getTime() / 30F % 360F + 45} 作为绕 Y 轴旋转角度。完整周期 = 360 × 30 = {@code 10800} 单位。
 *
 * <p>
 * 配套 mixin：{@code mixins.galacticraft.ItemRendererTier3RocketMixin} 把 {@code Sys.getTime()} 重定向到
 * {@link moe.takochan.webnei.exporter.domain.asset.render.hook.WebneiTimeSource}。
 *
 * <p>
 * 采样：{@link #sampleCount} 60 帧 × {@code stepMillis=180} = 10800 一个完整周期，每帧 6° 闭环。
 */
public final class GalacticraftTier3RocketHook extends AbstractPlayerTickHook {

    /** stepMillis × sampleCount = 10800 完整周期。 */
    private static final int STEP_MILLIS = 180;

    @Override
    public boolean isAvailable() {
        return Mods.GALACTICRAFT_PLANETS.isLoaded();
    }

    @Override
    protected boolean matchesItem(ItemStack stack) {
        IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.INVENTORY);
        return renderer instanceof ItemRendererTier3Rocket;
    }

    @Override
    public ITimeDriverSession begin() {
        return new WallclockSession(STEP_MILLIS);
    }
}
