package moe.takochan.webnei.exporter.hook.galaxyspace;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.common.Loader;
import galaxyspace.GalaxySpace;
import galaxyspace.core.render.item.ItemRendererThermalPaddingT2;
import moe.takochan.webnei.exporter.domain.asset.render.hook.AbstractPlayerTickHook;

/**
 * Galaxy Space 热防护内衬第二层颜色呼吸：与 Galacticraft 套路一致——读
 * {@code FMLClientHandler.instance().getClientPlayerEntity().ticksExisted / 15.0F} 余弦驱动。
 */
public final class GalaxySpaceThermalPaddingHook extends AbstractPlayerTickHook {

    @Override
    public boolean isAvailable() {
        return Loader.isModLoaded(GalaxySpace.MODID);
    }

    @Override
    protected boolean matchesItem(ItemStack stack) {
        IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.INVENTORY);
        return renderer instanceof ItemRendererThermalPaddingT2;
    }
}
