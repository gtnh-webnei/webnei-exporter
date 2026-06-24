package moe.takochan.webnei.exporter.hook.galacticraft;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.common.Loader;
import gregtech.api.enums.Mods;
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.item.ItemRendererThermalArmor;
import moe.takochan.webnei.exporter.domain.asset.render.hook.AbstractPlayerTickHook;

/**
 * Galacticraft 热防护服第二层颜色呼吸：渲染读
 * {@code FMLClientHandler.instance().getClientPlayerEntity().ticksExisted / 15.0F} 求 {@code Math.cos}，
 * 在 {@code (r,b)} 通道上做余弦呼吸。
 */
public final class GalacticraftThermalArmorHook extends AbstractPlayerTickHook {

    @Override
    public boolean isAvailable() {
        return Loader.isModLoaded(Mods.GalacticraftCore.ID);
    }

    @Override
    protected boolean matchesItem(ItemStack stack) {
        IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.INVENTORY);
        return renderer instanceof ItemRendererThermalArmor;
    }
}
