package moe.takochan.webnei.exporter.hook.cosmic;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.common.Loader;
import fox.spiteful.avaritia.render.CosmicBowRenderer;
import fox.spiteful.avaritia.render.CosmicItemRenderer;
import moe.takochan.webnei.exporter.domain.asset.AssetContract;
import moe.takochan.webnei.exporter.domain.asset.render.AssetRenderJob;
import moe.takochan.webnei.exporter.domain.asset.render.hook.AbstractPlayerTickHook;
import singulariteam.eternalsingularity.Reference;
import singulariteam.eternalsingularity.render.EternalItemRenderer;

/**
 * Avaritia / Eternal Singularity 共用的 cosmic shader：渲染时上传 {@code time2 = player.ticksExisted}
 * 作为 uniform，颜色随之流动。物品端通过 {@link IItemRenderer} 类型识别，流体端目前仅 universium 走此路径
 * （GT 的 {@code UniversiumRenderer} 同样走 cosmic shader，没有公开类型可 instanceof，按 ownerId 命中）。
 */
public final class CosmicShaderHook extends AbstractPlayerTickHook {

    private static final String UNIVERSIUM_FLUID_ID = "gregtech:molten.universium";

    @Override
    public boolean isAvailable() {
        // Avaritia 主类未暴露 modid 常量，回落到 @Mod 注解上的字面量；Eternal Singularity 走自己的常量。
        return Loader.isModLoaded("Avaritia") || Loader.isModLoaded(Reference.MOD_ID);
    }

    @Override
    public boolean applies(AssetRenderJob job) {
        if (job.getFluidStack() != null && AssetContract.KIND_FLUID_ICON.equals(job.getKind())) {
            return UNIVERSIUM_FLUID_ID.equals(job.getOwnerId());
        }
        return super.applies(job);
    }

    @Override
    protected boolean matchesItem(ItemStack stack) {
        IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.INVENTORY);
        return renderer instanceof CosmicItemRenderer || renderer instanceof CosmicBowRenderer
            || renderer instanceof EternalItemRenderer;
    }
}
