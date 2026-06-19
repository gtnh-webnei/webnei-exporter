package moe.takochan.webnei.exporter.domain.asset.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.asset.AssetExportModel;
import moe.takochan.webnei.exporter.domain.asset.model.AssetRow;

/** asset domain store 的内部数据和去重逻辑。 */
public final class AssetDomainData {

    private static final int ICON_SIZE = 16;
    private static final String EMPTY_SHA256 = "";
    private static final String OWNER_TYPE_ITEM_VARIANT = "item_variant";
    private static final String OWNER_TYPE_FLUID = "fluid";
    private static final String KIND_ITEM_ICON = "item_icon";
    private static final String KIND_FLUID_ICON = "fluid_icon";

    private final String datasetId;
    private final Map<String, AssetRow> assets = new LinkedHashMap<>();

    public AssetDomainData(String datasetId) {
        this.datasetId = datasetId;
    }

    public void registerItemIcon(String itemVariantId, ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return;
        }
        register(
            OWNER_TYPE_ITEM_VARIANT,
            itemVariantId,
            KIND_ITEM_ICON,
            AssetPath.itemIcon(itemVariantId),
            ICON_SIZE,
            ICON_SIZE);
    }

    public void registerFluidIcon(String fluidId, FluidStack stack) {
        if (stack == null || stack.getFluid() == null) {
            return;
        }
        register(OWNER_TYPE_FLUID, fluidId, KIND_FLUID_ICON, AssetPath.fluidIcon(fluidId), ICON_SIZE, ICON_SIZE);
    }

    public IExportModel toExportModel() {
        return new AssetExportModel(new ArrayList<>(assets.values()));
    }

    private void register(String ownerType, String ownerId, String kind, String path, int width, int height) {
        if (ownerId == null || ownerId.isEmpty()) {
            return;
        }
        String key = ownerType + '\u0000' + ownerId + '\u0000' + kind;
        assets.putIfAbsent(key, new AssetRow(datasetId, ownerType, ownerId, kind, path, EMPTY_SHA256, width, height));
    }
}
