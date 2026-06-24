package moe.takochan.webnei.exporter.hook.galaxyspace;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import galaxyspace.core.render.item.ItemRendererThermalPaddingT2;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.asset.render.hook.AbstractPlayerTickHook;

/**
 * GalaxySpace {@code ItemRendererThermalPaddingT2}：与 Galacticraft {@code ItemRendererThermalArmor} 公式相同——
 * 第二层色值随 {@code cos(getClientPlayerEntity().ticksExisted / 15.0F)} 周期变化。
 *
 * <p>
 * 完整 cos 周期 = {@code 2π × 15 ≈ 94.25 tick}，override {@link #sampleCount} 返回 94 让 spritesheet 闭环。
 */
public final class GalaxySpaceThermalPaddingHook extends AbstractPlayerTickHook {

    /** cos(ticksExisted / 15) 一个完整周期约 94 tick。 */
    private static final int COS_PERIOD = 94;

    @Override
    public int sampleCount() {
        return COS_PERIOD;
    }

    @Override
    public boolean isAvailable() {
        return Mods.GALAXY_SPACE.isLoaded();
    }

    @Override
    protected boolean matchesItem(ItemStack stack) {
        IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.INVENTORY);
        return renderer instanceof ItemRendererThermalPaddingT2;
    }
}
