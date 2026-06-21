package moe.takochan.webnei.exporter.domain.asset.internal;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public final class AssetRegistrar {

    private final AssetDomainData data;

    public AssetRegistrar(AssetDomainData data) {
        this.data = data;
    }

    public void registerItemIcon(String itemVariantId, ItemStack stack) {
        data.registerItemIcon(itemVariantId, stack);
    }

    public void registerFluidIcon(String fluidId, FluidStack stack) {
        data.registerFluidIcon(fluidId, stack);
    }

    public void registerRecipeCategoryIcon(String categoryId, ItemStack stack) {
        data.registerRecipeCategoryIcon(categoryId, stack);
    }
}
