package moe.takochan.webnei.exporter.hook.draconicevolution;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import com.brandon3055.draconicevolution.client.render.block.RenderEarthItem;

import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.asset.render.hook.AbstractPlayerTickHook;
import moe.takochan.webnei.exporter.domain.asset.render.hook.ITimeDriverSession;
import moe.takochan.webnei.exporter.domain.asset.render.hook.WallclockSession;

/**
 * Draconic Evolution {@code RenderEarthItem}：渲染时读 {@code (System.currentTimeMillis() / 64) % 360}
 * 作为绕 Z 轴旋转角度。完整周期 = 360 × 64ms = {@code 23040ms}。
 *
 * <p>
 * 配套 mixin：{@code mixins.draconicevolution.RenderEarthItemMixin} 把
 * {@code System.currentTimeMillis} 重定向到
 * {@link moe.takochan.webnei.exporter.domain.asset.render.hook.WebneiTimeSource}。
 *
 * <p>
 * 采样：{@link #sampleCount} 60 帧 × {@code stepMillis=384} = 23040ms 一个完整周期，每帧 6° 闭环。
 */
public final class DraconicEvolutionEarthItemHook extends AbstractPlayerTickHook {

    /** stepMillis × sampleCount = 23040ms 完整周期。 */
    private static final int STEP_MILLIS = 384;

    @Override
    public boolean isAvailable() {
        return Mods.DRACONIC_EVOLUTION.isLoaded();
    }

    @Override
    protected boolean matchesItem(ItemStack stack) {
        IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.INVENTORY);
        return renderer instanceof RenderEarthItem;
    }

    @Override
    public ITimeDriverSession begin() {
        return new WallclockSession(STEP_MILLIS);
    }
}
