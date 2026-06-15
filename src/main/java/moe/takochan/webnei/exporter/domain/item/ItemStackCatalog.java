package moe.takochan.webnei.exporter.domain.item;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.adapter.AdapterContext;
import moe.takochan.webnei.exporter.adapter.AdapterRegistry;
import moe.takochan.webnei.exporter.domain.asset.AssetRequestRegistry;
import moe.takochan.webnei.exporter.domain.dataset.task.DatasetIdentity;
import moe.takochan.webnei.exporter.domain.item.model.ItemRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemToolClassRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.domain.item.model.NeiItemPanelEntryRow;

/**
 * 同一次导出内的 ItemStack 统一登记中心。
 *
 * <p>
 * 任何数据域只要遇到真实 {@link ItemStack}，都应调用 {@link #register(ItemStack)}。这里按
 * registry id + raw meta + canonical NBT 去重，并一次性维护 item、item_variant、item_tool_class 三类领域行。
 * NEI panel 只是展示索引，不能绕过这里单独创建 item 数据。
 */
public final class ItemStackCatalog {

    private final DatasetIdentity dataset;
    private final IItemIdentityResolver identityResolver;
    private final IItemDetailCollector detailCollector;
    private final IItemToolClassCollector toolClassCollector;
    private final AssetRequestRegistry assetRequestRegistry;
    private final AdapterRegistry adapterRegistry;
    private final AdapterContext adapterContext;
    private final Map<String, ItemRow> items = new LinkedHashMap<>();
    private final Map<String, ItemVariantRow> variants = new LinkedHashMap<>();
    private final List<ItemToolClassRow> toolClasses = new ArrayList<>();
    private final Map<String, NeiItemPanelEntryRow> panelEntries = new LinkedHashMap<>();
    private final Set<String> toolClassKeys = new LinkedHashSet<>();

    public ItemStackCatalog(DatasetIdentity dataset, IItemIdentityResolver identityResolver,
        IItemDetailCollector detailCollector, IItemToolClassCollector toolClassCollector,
        AssetRequestRegistry assetRequestRegistry, AdapterRegistry adapterRegistry, AdapterContext adapterContext) {
        this.dataset = dataset;
        this.identityResolver = identityResolver;
        this.detailCollector = detailCollector;
        this.toolClassCollector = toolClassCollector;
        this.assetRequestRegistry = assetRequestRegistry;
        this.adapterRegistry = adapterRegistry;
        this.adapterContext = adapterContext;
    }

    /**
     * 登记一个 ItemStack，并返回稳定的 item_variant_id。
     *
     * <p>
     * 本方法是 item 数据域的唯一写入入口：它会确保基础 item 行、具体 variant 行、tool class 行、asset request
     * 和 adapter 补充字段都以同一个 ItemStack 身份创建。调用方不要用对象引用做匹配。
     */
    public String register(ItemStack stack) {
        ItemStack copy = stack.copy();
        ItemIdentity itemIdentity = identityResolver.resolveItem(copy.getItem());
        ItemVariantIdentity variantIdentity = identityResolver.resolveVariant(copy);
        ensureItem(itemIdentity, copy);
        ensureVariant(itemIdentity, variantIdentity, copy);
        return variantIdentity.getItemVariantId();
    }

    /** 记录 NEI 面板展示入口；这里只写展示索引，不创建 item/item_variant 数据。 */
    public void addPanelEntry(String itemVariantId, int panelIndex) {
        if (!panelEntries.containsKey(itemVariantId)) {
            panelEntries.put(
                itemVariantId,
                new NeiItemPanelEntryRow(dataset.getDatasetId(), itemVariantId, panelIndex, "", true));
        }
    }

    public ItemExportModel toModel() {
        return new ItemExportModel(
            new ArrayList<>(items.values()),
            new ArrayList<>(variants.values()),
            new ArrayList<>(toolClasses),
            new ArrayList<>(panelEntries.values()));
    }

    private void ensureItem(ItemIdentity identity, ItemStack stack) {
        if (!items.containsKey(identity.getItemId())) {
            items.put(identity.getItemId(), detailCollector.collectItem(dataset, identity, stack));
        }
    }

    private void ensureVariant(ItemIdentity itemIdentity, ItemVariantIdentity variantIdentity, ItemStack stack) {
        if (!variants.containsKey(variantIdentity.getItemVariantId())) {
            String assetId = assetRequestRegistry.requestItemIcon(stack, itemIdentity, variantIdentity);
            ItemVariantRow row = detailCollector.collectVariant(dataset, variantIdentity, stack, assetId);
            adapterRegistry.fillItemVariant(stack, row, adapterContext);
            variants.put(variantIdentity.getItemVariantId(), row);
            addToolClasses(variantIdentity, stack);
        }
    }

    private void addToolClasses(ItemVariantIdentity variant, ItemStack stack) {
        for (ItemToolClassRow row : toolClassCollector.collect(dataset, variant, stack)) {
            String key = row.getItemVariantId() + '\u0000' + row.getToolClass();
            if (toolClassKeys.add(key)) {
                toolClasses.add(row);
            }
        }
    }
}
