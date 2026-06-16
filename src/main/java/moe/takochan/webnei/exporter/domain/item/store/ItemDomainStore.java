package moe.takochan.webnei.exporter.domain.item.store;

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
import moe.takochan.webnei.exporter.domain.item.internal.ForgeItemIdentityResolver;
import moe.takochan.webnei.exporter.domain.item.internal.ItemIdentity;
import moe.takochan.webnei.exporter.domain.item.internal.ItemStackDetailCollector;
import moe.takochan.webnei.exporter.domain.item.internal.ItemToolClassCollector;
import moe.takochan.webnei.exporter.domain.item.internal.ItemVariantIdentity;
import moe.takochan.webnei.exporter.domain.item.model.ItemListEntryRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemToolClassRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/**
 * item domain store — 保存 item 数据，并作为其他 domain 获取/补充 item variant 的唯一入口。
 */
public final class ItemDomainStore implements IDomainStore {

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

    public ItemDomainStore(String datasetId) {
        this.datasetId = datasetId;
    }

    /**
     * 获取 stack 对应 variant；不存在时补齐 item、variant 和 toolclass，不写 item_list_entry。
     */
    public ItemVariantRow getOrRegisterVariant(ItemStack input) {
        ItemStack stack = input.copy();
        ItemIdentity itemIdentity = identityResolver.resolveItem(stack.getItem());
        ItemVariantIdentity variantIdentity = identityResolver.resolveVariant(stack, itemIdentity);

        ensureItem(itemIdentity, stack);
        return ensureVariant(variantIdentity, stack);
    }

    /**
     * 返回已注册 variant 对应的原始 ItemStack。
     */
    public Map<String, ItemStack> stacks() {
        return Collections.unmodifiableMap(stacks);
    }

    /**
     * 注册 NEI item panel 条目：先补齐 item、variant、toolclass，再记录展示顺序。
     */
    public void registerListEntry(ItemStack stack, int listIndex) {
        ItemVariantRow row = getOrRegisterVariant(stack);
        addListEntry(row.getItemVariantId(), listIndex);
    }

    @Override
    public IExportModel toExportModel() {
        return new ItemExportModel(
            new ArrayList<>(items.values()),
            new ArrayList<>(variants.values()),
            new ArrayList<>(toolClasses),
            new ArrayList<>(listEntries.values()));
    }

    /**
     * 首次遇到该 item 时采集基础信息。
     */
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

    /**
     * 注册新的 item variant，并同步维护原始 stack 和 toolclass。
     */
    private ItemVariantRow registerVariant(ItemVariantIdentity identity, ItemStack stack) {
        ItemVariantRow row = detailCollector.collectVariant(datasetId, identity, stack);
        itemVariantHooks.enrich(stack, row);
        variants.put(identity.getItemVariantId(), row);
        stacks.put(identity.getItemVariantId(), stack);
        addToolClasses(identity, stack);
        return row;
    }

    /**
     * 写入 NEI 展示顺序。
     */
    private void addListEntry(String itemVariantId, int listIndex) {
        if (!listEntries.containsKey(itemVariantId)) {
            listEntries.put(itemVariantId, new ItemListEntryRow(datasetId, itemVariantId, listIndex));
        }
    }

    /**
     * 采集 variant 的工具类型。
     */
    private void addToolClasses(ItemVariantIdentity variant, ItemStack stack) {
        for (ItemToolClassRow row : toolClassCollector.collect(datasetId, variant, stack)) {
            addToolClass(row);
        }
    }

    /**
     * 写入工具类型并按 variant + tool class 去重。
     */
    private void addToolClass(ItemToolClassRow row) {
        String key = row.getItemVariantId() + '\u0000' + row.getToolClass();
        if (toolClassKeys.add(key)) {
            toolClasses.add(row);
        }
    }
}
