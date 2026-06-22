package moe.takochan.webnei.exporter.domain.asset.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.asset.AssetExportModel;
import moe.takochan.webnei.exporter.domain.asset.render.AssetRenderJob;
import moe.takochan.webnei.exporter.engine.store.IDomainData;

public final class AssetDomainData implements IDomainData {

    private final String datasetId;
    private final Map<String, AssetRenderJob> renderJobs = new LinkedHashMap<>();

    public AssetDomainData(String datasetId) {
        this.datasetId = datasetId;
    }

    void putItemIcon(String itemVariantId, ItemStack stack) {
        if (stack == null || stack.getItem() == null || itemVariantId == null || itemVariantId.isEmpty()) {
            return;
        }
        put(AssetRenderJob.itemIcon(datasetId, itemVariantId, stack));
    }

    void putFluidIcon(String fluidId, FluidStack stack) {
        if (stack == null || stack.getFluid() == null || fluidId == null || fluidId.isEmpty()) {
            return;
        }
        put(AssetRenderJob.fluidIcon(datasetId, fluidId, stack));
    }

    void putRecipeCategoryIcon(String categoryId, ItemStack stack) {
        if (stack == null || stack.getItem() == null || categoryId == null || categoryId.isEmpty()) {
            return;
        }
        put(AssetRenderJob.recipeCategoryIcon(datasetId, categoryId, stack));
    }

    @Override
    public IExportModel toExportModel() {
        return AssetExportModel.pending(new ArrayList<>(renderJobs.values()));
    }

    private void put(AssetRenderJob job) {
        renderJobs.putIfAbsent(job.key(), job);
    }
}
