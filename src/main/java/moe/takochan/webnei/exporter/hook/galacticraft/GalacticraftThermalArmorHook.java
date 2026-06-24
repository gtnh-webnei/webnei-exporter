package moe.takochan.webnei.exporter.hook.galacticraft;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.common.Loader;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.item.ItemRendererThermalArmor;
import moe.takochan.webnei.exporter.domain.asset.render.hook.AbstractPlayerTickHook;

/**
 * Galacticraft {@code ItemRendererThermalArmor}：第二层（i==1）色值随
 * {@code cos(getClientPlayerEntity().ticksExisted / 15.0F)} 周期变化，在 (r, b) 通道上做 cos 抖动。
 *
 * <p>
 * 完整 cos 周期 = {@code 2π × 15 ≈ 94.25 tick}，override {@link #sampleCount} 返回 94 让 spritesheet 闭环。
 */
public final class GalacticraftThermalArmorHook extends AbstractPlayerTickHook {

    /** cos(ticksExisted / 15) 一个完整周期约 94 tick。 */
    private static final int COS_PERIOD = 94;

    @Override
    public int sampleCount() {
        return COS_PERIOD;
    }

    @Override
    public boolean isAvailable() {
        // ItemRendererThermalArmor 在 planets.asteroids 子 mod，由 GalacticraftMars 注册。
        return Loader.isModLoaded(Constants.MOD_ID_PLANETS);
    }

    @Override
    protected boolean matchesItem(ItemStack stack) {
        IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.INVENTORY);
        return renderer instanceof ItemRendererThermalArmor;
    }
}
