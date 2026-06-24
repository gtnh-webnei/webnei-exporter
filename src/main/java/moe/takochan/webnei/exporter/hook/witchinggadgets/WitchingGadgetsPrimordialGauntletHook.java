package moe.takochan.webnei.exporter.hook.witchinggadgets;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.common.Loader;
import gregtech.api.enums.Mods;
import moe.takochan.webnei.exporter.domain.asset.render.hook.AbstractPlayerTickHook;
import witchinggadgets.client.render.ItemRenderPrimordialGauntlet;

/**
 * Witching Gadgets 原初手套：手指 overlay 与 rune 颜色由 {@code mc.thePlayer.ticksExisted} 决定的
 * fingerOverlayColour 表索引滚动驱动。
 */
public final class WitchingGadgetsPrimordialGauntletHook extends AbstractPlayerTickHook {

    @Override
    public boolean isAvailable() {
        return Loader.isModLoaded(Mods.WitchingGadgets.ID);
    }

    @Override
    protected boolean matchesItem(ItemStack stack) {
        IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.INVENTORY);
        return renderer instanceof ItemRenderPrimordialGauntlet;
    }
}
