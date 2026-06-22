package moe.takochan.webnei.exporter.domain.asset.internal;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import codechicken.nei.drawable.DrawableResource;
import moe.takochan.webnei.exporter.engine.store.IDomainRegistrar;

public final class AssetRegistrar implements IDomainRegistrar {

    private final AssetDomainData data;

    public AssetRegistrar(AssetDomainData data) {
        this.data = data;
    }

    public void registerItemIcon(String itemVariantId, ItemStack stack) {
        data.putItemIcon(itemVariantId, stack);
    }

    public void registerFluidIcon(String fluidId, FluidStack stack) {
        data.putFluidIcon(fluidId, stack);
    }

    public void registerRecipeCategoryIcon(String categoryId, ItemStack stack) {
        data.putRecipeCategoryIcon(categoryId, stack);
    }

    public void registerRecipeCategoryImageIcon(String categoryId, DrawableResource image) {
        data.putRecipeCategoryImageIcon(categoryId, image);
    }

    public void registerRecipeCategoryTextIcon(String categoryId, String text) {
        data.putRecipeCategoryTextIcon(categoryId, text);
    }
}
