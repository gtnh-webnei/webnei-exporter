package moe.takochan.webnei.exporter.hook.witchinggadgets;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.common.Loader;
import moe.takochan.webnei.exporter.domain.asset.render.hook.AbstractPlayerTickHook;
import witchinggadgets.WitchingGadgets;
import witchinggadgets.client.render.ItemRenderPrimordialGauntlet;

/**
 * Witching Gadgets {@code ItemRenderPrimordialGauntlet}：手指 overlay 与 rune 颜色由
 * {@code mc.thePlayer.ticksExisted} 决定的 fingerOverlayColour 表索引滚动驱动。
 *
 * <p>
 * INVENTORY 路径关键：{@code colour = ticksExisted % 32 / 2}（手指 overlay）和
 * {@code runeColour = (ticksExisted % 32) + 1} 三角波（rune）——周期都是 32 tick。
 * override {@link #sampleCount} 返回 32 让 spritesheet 完整闭环且体积最小。
 */
public final class WitchingGadgetsPrimordialGauntletHook extends AbstractPlayerTickHook {

    /** fingerOverlay / rune 周期 = 32 tick。 */
    private static final int OVERLAY_PERIOD = 32;

    @Override
    public int sampleCount() {
        return OVERLAY_PERIOD;
    }

    @Override
    public boolean isAvailable() {
        return Loader.isModLoaded(WitchingGadgets.MODID);
    }

    @Override
    protected boolean matchesItem(ItemStack stack) {
        IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.INVENTORY);
        return renderer instanceof ItemRenderPrimordialGauntlet;
    }
}
