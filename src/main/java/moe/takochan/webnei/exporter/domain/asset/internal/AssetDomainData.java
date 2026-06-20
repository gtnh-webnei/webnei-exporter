package moe.takochan.webnei.exporter.domain.asset.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.asset.AssetExportModel;
import moe.takochan.webnei.exporter.domain.asset.render.AssetRenderJob;

/** asset domain store 的内部数据和去重逻辑。 */
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
        AssetRenderJob job = AssetRenderJob.itemIcon(datasetId, itemVariantId, stack);
        renderJobs.putIfAbsent(job.key(), job);
    }

    public void registerFluidIcon(String fluidId, FluidStack stack) {
        if (stack == null || stack.getFluid() == null || fluidId == null || fluidId.isEmpty()) {
            return;
        }
        AssetRenderJob job = AssetRenderJob.fluidIcon(datasetId, fluidId, stack);
        renderJobs.putIfAbsent(job.key(), job);
    }

    public IExportModel toExportModel() {
        return AssetExportModel.pending(new ArrayList<>(renderJobs.values()));
    }
}
