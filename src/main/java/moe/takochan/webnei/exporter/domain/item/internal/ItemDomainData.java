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
import moe.takochan.webnei.exporter.domain.item.model.ItemListEntryRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemToolClassRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.engine.store.IDomainData;

/**
 * item domain store 的内部结果集。
 *
 * <p>
 * 该类只持有 item/variant/toolclass/list entry 结果集；注册编排职责由 ItemRegistrar 负责。
 */
public final class ItemDomainData implements IDomainData {

    private final Map<String, ItemRow> items = new LinkedHashMap<>();
    private final Map<String, ItemVariantRow> variants = new LinkedHashMap<>();
    private final Map<String, ItemStack> stacks = new LinkedHashMap<>();
    private final List<ItemToolClassRow> toolClasses = new ArrayList<>();
    private final Set<String> toolClassKeys = new LinkedHashSet<>();
    private final Map<String, ItemListEntryRow> listEntries = new LinkedHashMap<>();

    /** 返回已注册 variant 对应的原始 ItemStack。 */
    public Map<String, ItemStack> stacks() {
        return Collections.unmodifiableMap(stacks);
    }

    ItemRow findItem(String itemId) {
        return items.get(itemId);
    }

    ItemVariantRow findVariant(String itemVariantId) {
        return variants.get(itemVariantId);
    }

    void putItem(ItemRow row) {
        String itemId = row.getItemId();
        items.putIfAbsent(itemId, row);
    }


    void putVariant(ItemVariantRow row, ItemStack stack) {
        String itemVariantId = row.getItemVariantId();
        variants.put(itemVariantId, row);
        stacks.put(itemVariantId, stack);
    }


    void putListEntry(ItemListEntryRow row) {
        if (!listEntries.containsKey(row.getItemVariantId())) {
            listEntries.put(row.getItemVariantId(), row);
        }
    }

    void putToolClass(ItemToolClassRow row) {
        String key = row.getItemVariantId() + '\u0000' + row.getToolClass();
        if (toolClassKeys.add(key)) {
            toolClasses.add(row);
        }
    }

    @Override
    public IExportModel toExportModel() {
        return new ItemExportModel(
            new ArrayList<>(items.values()),
            new ArrayList<>(variants.values()),
            new ArrayList<>(toolClasses),
            new ArrayList<>(listEntries.values()));
    }
}
