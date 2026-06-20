package moe.takochan.webnei.exporter.domain.asset.store;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.asset.internal.AssetDomainData;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

public final class AssetDomainStore implements IDomainStore {

    private final AssetDomainData data;

    public AssetDomainStore(String datasetId) {
        this.data = new AssetDomainData(datasetId);
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

    @Override
    public IExportModel toExportModel() {
        return data.toExportModel();
    }
}
