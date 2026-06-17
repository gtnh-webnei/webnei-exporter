package moe.takochan.webnei.exporter.domain.item.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.item.ItemExportModel;
import moe.takochan.webnei.exporter.domain.item.hook.ItemVariantHookRegistry;
import moe.takochan.webnei.exporter.domain.item.model.ItemListEntryRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemToolClassRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;

/**
 * item domain store 的内部数据和注册逻辑。
 *
 * <p>
 * 该类负责维护 item、variant、toolclass、list entry 等具体 row 集合；跨 domain API 由 ItemDomainStore 包装后暴露。
 */
public final class ItemDomainData {

    private final String datasetId;
    private final ForgeItemIdentityResolver identityResolver = new ForgeItemIdentityResolver();
    private final ItemStackDetailCollector detailCollector = new ItemStackDetailCollector();
    private final ItemToolClassCollector toolClassCollector = new ItemToolClassCollector();
    private final ItemVariantHookRegistry itemVariantHooks = new ItemVariantHookRegistry();
    private final Map<String, ItemRow> items = new LinkedHashMap<>();
    private final Map<String, ItemVariantRow> variants = new LinkedHashMap<>();
    private final Map<String, ItemStack> stacks = new LinkedHashMap<>();
    private final List<ItemToolClassRow> toolClasses = new ArrayList<>();
    private final Set<String> toolClassKeys = new LinkedHashSet<>();
    private final Map<String, ItemListEntryRow> listEntries = new LinkedHashMap<>();

    public ItemDomainData(String datasetId) {
        this.datasetId = datasetId;
    }

    public ItemVariantRow getOrRegisterVariant(ItemStack input) {
        ItemStack stack = input.copy();
        ItemIdentity itemIdentity = identityResolver.resolveItem(stack.getItem());
        ItemVariantIdentity variantIdentity = identityResolver.resolveVariant(stack, itemIdentity);

        ensureItem(itemIdentity, stack);
        return ensureVariant(variantIdentity, stack);
    }

    public Map<String, ItemStack> stacks() {
        return Collections.unmodifiableMap(stacks);
    }

    public void registerListEntry(ItemStack stack, int listIndex) {
        ItemVariantRow row = getOrRegisterVariant(stack);
        addListEntry(row.getItemVariantId(), listIndex);
    }

    public IExportModel toExportModel() {
        return new ItemExportModel(
            new ArrayList<>(items.values()),
            new ArrayList<>(variants.values()),
            new ArrayList<>(toolClasses),
            new ArrayList<>(listEntries.values()));
    }

    private void ensureItem(ItemIdentity identity, ItemStack stack) {
        if (!items.containsKey(identity.getItemId())) {
            items.put(identity.getItemId(), detailCollector.collectItem(datasetId, identity, stack));
        }
    }

    private ItemVariantRow ensureVariant(ItemVariantIdentity identity, ItemStack stack) {
        ItemVariantRow existing = variants.get(identity.getItemVariantId());
        if (existing != null) {
            return existing;
        }

        return registerVariant(identity, stack);
    }

    private ItemVariantRow registerVariant(ItemVariantIdentity identity, ItemStack stack) {
        ItemVariantRow row = detailCollector.collectVariant(datasetId, identity, stack);
        itemVariantHooks.enrich(stack, row);
        variants.put(identity.getItemVariantId(), row);
        stacks.put(identity.getItemVariantId(), stack);
        addToolClasses(identity, stack);
        return row;
    }

    private void addListEntry(String itemVariantId, int listIndex) {
        if (!listEntries.containsKey(itemVariantId)) {
            listEntries.put(itemVariantId, new ItemListEntryRow(datasetId, itemVariantId, listIndex));
        }
    }

    private void addToolClasses(ItemVariantIdentity variant, ItemStack stack) {
        for (ItemToolClassRow row : toolClassCollector.collect(datasetId, variant, stack)) {
            addToolClass(row);
        }
    }

    private void addToolClass(ItemToolClassRow row) {
        String key = row.getItemVariantId() + '\u0000' + row.getToolClass();
        if (toolClassKeys.add(key)) {
            toolClasses.add(row);
        }
    }
}
