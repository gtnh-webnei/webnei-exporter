package moe.takochan.webnei.exporter.domain.asset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.item.internal.ItemIdentity;
import moe.takochan.webnei.exporter.domain.item.internal.ItemVariantIdentity;

/** 收集跨数据域的资源渲染请求，供后续 AssetExportStep 统一消费。 */
public final class AssetRequestRegistry {

    private final AssetIdFactory assetIdFactory;
    private final List<AssetRequest> requests = new ArrayList<>();

    public AssetRequestRegistry(AssetIdFactory assetIdFactory) {
        this.assetIdFactory = assetIdFactory;
    }

    public String requestItemIcon(ItemStack stack, ItemIdentity item, ItemVariantIdentity variant) {
        String assetId = assetIdFactory.itemIcon(item, variant);
        requests.add(new AssetRequest("item_variant", variant.getItemVariantId(), assetId, stack.copy()));
        return assetId;
    }

    public List<AssetRequest> requests() {
        return Collections.unmodifiableList(requests);
    }
}
