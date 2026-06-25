package moe.takochan.webnei.exporter.coremod.mixin.galacticraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import micdoodle8.mods.galacticraft.planets.asteroids.client.render.item.ItemRendererTier3Rocket;
import moe.takochan.webnei.exporter.domain.asset.render.hook.WebneiTimeSource;

/**
 * 把 {@link ItemRendererTier3Rocket#transform} 内部对 {@code org.lwjgl.Sys#getTime()} 的调用重定向到
 * {@link WebneiTimeSource}，使导出器能在采样窗口内驱动火箭绕 Y 轴旋转动画。
 *
 * <p>
 * 注意：{@code Sys.getTime()} 的调用点在 {@code transform} 方法（{@code renderItem → renderSpaceship →
 * transform}），不在 {@code renderItem} 本身——必须以 {@code transform} 为注入目标。
 *
 * <p>
 * 时间公式：{@code Sys.getTime() / 30F % 360F + 45}，完整周期 = 360 × 30 = 10800 单位。
 */
@Mixin(value = ItemRendererTier3Rocket.class, remap = false)
public abstract class ItemRendererTier3RocketMixin {

    @Redirect(method = "transform", at = @At(value = "INVOKE", target = "Lorg/lwjgl/Sys;getTime()J", remap = false))
    private long webnei$redirectSysGetTime() {
        return WebneiTimeSource.sysGetTime();
    }
}
