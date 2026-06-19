package moe.takochan.webnei.exporter.domain.asset.store;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.asset.internal.AssetDomainData;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/** asset domain store — asset domain 的跨 domain 交互边界。 */
public final class AssetDomainStore implements IDomainStore {

    private final AssetDomainData data;

    public AssetDomainStore(String datasetId) {
        this.data = new AssetDomainData(datasetId);
    }

    /** 注册 item variant 的 inventory icon asset。 */
    public void registerItemIcon(String itemVariantId, ItemStack stack) {
        data.registerItemIcon(itemVariantId, stack);
    }

    /** 注册 fluid 的 icon asset。 */
    public void registerFluidIcon(String fluidId, FluidStack stack) {
        data.registerFluidIcon(fluidId, stack);
    }

    @Override
    public IExportModel toExportModel() {
        return data.toExportModel();
    }
}
