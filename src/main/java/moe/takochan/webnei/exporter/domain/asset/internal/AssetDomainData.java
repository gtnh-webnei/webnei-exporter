package moe.takochan.webnei.exporter.domain.asset.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.asset.AssetExportModel;
import moe.takochan.webnei.exporter.domain.asset.render.AssetRenderJob;

public final class AssetDomainData {

    private final String datasetId;
    private final Map<String, AssetRenderJob> renderJobs = new LinkedHashMap<>();

    public AssetDomainData(String datasetId) {
        this.datasetId = datasetId;
    }

    public void registerItemIcon(String itemVariantId, ItemStack stack) {
        if (stack == null || stack.getItem() == null || itemVariantId == null || itemVariantId.isEmpty()) {
            return;
        }
        put(AssetRenderJob.itemIcon(datasetId, itemVariantId, stack));
    }

    public void registerFluidIcon(String fluidId, FluidStack stack) {
        if (stack == null || stack.getFluid() == null || fluidId == null || fluidId.isEmpty()) {
            return;
        }
        put(AssetRenderJob.fluidIcon(datasetId, fluidId, stack));
    }

    public void registerRecipeCategoryIcon(String categoryId, ItemStack stack) {
        if (stack == null || stack.getItem() == null || categoryId == null || categoryId.isEmpty()) {
            return;
        }
        put(AssetRenderJob.recipeCategoryIcon(datasetId, categoryId, stack));
    }

    public IExportModel toExportModel() {
        return AssetExportModel.pending(new ArrayList<>(renderJobs.values()));
    }

    private void put(AssetRenderJob job) {
        renderJobs.putIfAbsent(job.key(), job);
    }
}
