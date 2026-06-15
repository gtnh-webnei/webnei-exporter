package moe.takochan.webnei.exporter.domain.item.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.adapter.AdapterContext;
import moe.takochan.webnei.exporter.adapter.AdapterRegistry;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.item.ItemExportModel;
import moe.takochan.webnei.exporter.domain.item.internal.ForgeItemIdentityResolver;
import moe.takochan.webnei.exporter.domain.item.internal.ItemIdentity;
import moe.takochan.webnei.exporter.domain.item.internal.ItemStackDetailCollector;
import moe.takochan.webnei.exporter.domain.item.internal.ItemToolClassCollector;
import moe.takochan.webnei.exporter.domain.item.internal.ItemVariantIdentity;
import moe.takochan.webnei.exporter.domain.item.model.ItemRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemToolClassRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemListEntryRow;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/**
 * item domain store。
 *
 * <p>
 * 输入 ItemStack，内部处理身份解析、去重、字段采集和 adapter 补充，
 * 输出 ItemVariantRow。任何 domain 遇到真实 ItemStack 时都应调用 {@link #add}。
 */
public final class ItemDomainStore implements IDomainStore<ItemStack, ItemVariantRow> {

    private final String datasetId;
    private final ForgeItemIdentityResolver identityResolver;
    private final ItemStackDetailCollector detailCollector;
    private final ItemToolClassCollector toolClassCollector;
    private final AdapterRegistry adapterRegistry;
    private final AdapterContext adapterContext;

    private final Map<String, ItemRow> items = new LinkedHashMap<>();
    private final Map<String, ItemVariantRow> variants = new LinkedHashMap<>();
    private final List<ItemToolClassRow> toolClasses = new ArrayList<>();
    private final Map<String, ItemListEntryRow> listEntries = new LinkedHashMap<>();
    private final Set<String> toolClassKeys = new LinkedHashSet<>();

    public ItemDomainStore(String datasetId) {
        this.datasetId = datasetId;
        this.identityResolver = new ForgeItemIdentityResolver();
        this.detailCollector = new ItemStackDetailCollector();
        this.toolClassCollector = new ItemToolClassCollector();
        this.adapterRegistry = AdapterRegistry.defaults();
        this.adapterContext = new AdapterContext();
    }

    @Override
    public ItemVariantRow add(ItemStack input) {
        ItemStack copy = input.copy();
        ItemIdentity itemIdentity = identityResolver.resolveItem(copy.getItem());
        ItemVariantIdentity variantIdentity = identityResolver.resolveVariant(copy);
        ensureItem(itemIdentity, copy);
        return ensureVariant(itemIdentity, variantIdentity, copy);
    }

    @Override
    public ItemVariantRow get(String key) {
        return variants.get(key);
    }

    @Override
    public List<ItemVariantRow> list() {
        return Collections.unmodifiableList(new ArrayList<>(variants.values()));
    }

    @Override
    public IExportModel toExportModel() {
        return new ItemExportModel(
            new ArrayList<>(items.values()),
            new ArrayList<>(variants.values()),
            new ArrayList<>(toolClasses),
            new ArrayList<>(listEntries.values()));
    }

    /** 记录物品列表展示入口。 */
    public void addListEntry(String itemVariantId, int listIndex) {
        if (!listEntries.containsKey(itemVariantId)) {
            listEntries.put(
                itemVariantId,
                new ItemListEntryRow(datasetId, itemVariantId, listIndex));
        }
    }

    private void ensureItem(ItemIdentity identity, ItemStack stack) {
        if (!items.containsKey(identity.getItemId())) {
            items.put(identity.getItemId(), detailCollector.collectItem(datasetId, identity, stack));
        }
    }

    private ItemVariantRow ensureVariant(ItemIdentity itemIdentity, ItemVariantIdentity variantIdentity,
        ItemStack stack) {
        ItemVariantRow existing = variants.get(variantIdentity.getItemVariantId());
        if (existing != null) {
            return existing;
        }
        ItemVariantRow row = detailCollector.collectVariant(datasetId, variantIdentity, stack);
        adapterRegistry.fillItemVariant(stack, row, adapterContext);
        variants.put(variantIdentity.getItemVariantId(), row);
        addToolClasses(variantIdentity, stack);
        return row;
    }

    private void addToolClasses(ItemVariantIdentity variant, ItemStack stack) {
        for (ItemToolClassRow row : toolClassCollector.collect(datasetId, variant, stack)) {
            String key = row.getItemVariantId() + '\u0000' + row.getToolClass();
            if (toolClassKeys.add(key)) {
                toolClasses.add(row);
            }
        }
    }
}
