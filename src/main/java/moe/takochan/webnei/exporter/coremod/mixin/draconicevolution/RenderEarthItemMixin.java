package moe.takochan.webnei.exporter.coremod.mixin.draconicevolution;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.brandon3055.draconicevolution.client.render.block.RenderEarthItem;

import moe.takochan.webnei.exporter.domain.asset.render.hook.WebneiTimeSource;

/**
 * 把 {@link RenderEarthItem#renderItem} 内部对 {@code System.currentTimeMillis()} 的调用重定向到
 * {@link WebneiTimeSource}，使导出器能在采样窗口内驱动地球绕 Z 轴旋转动画。
 *
 * <p>
 * 时间公式：{@code (currentTimeMillis() / 64) % 360}（每 64ms 旋转 1°），完整周期 = 360 × 64ms = 23040ms。
 */
@Mixin(value = RenderEarthItem.class, remap = false)
public abstract class RenderEarthItemMixin {

    @Redirect(
        method = "renderItem",
        at = @At(value = "INVOKE", target = "Ljava/lang/System;currentTimeMillis()J", remap = false))
    private long webnei$redirectCurrentTimeMillis() {
        return WebneiTimeSource.currentTimeMillis();
    }
}
