package moe.takochan.webnei.exporter.domain.item.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.item.ItemExportModel;
import moe.takochan.webnei.exporter.domain.item.model.ItemRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemToolClassRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemListEntryRow;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/**
 * item domain store — 纯数据持有和对外查询接口。
 */
public final class ItemDomainStore implements IDomainStore {

    private final String datasetId;
    private final Map<String, ItemRow> items = new LinkedHashMap<>();
    private final Map<String, ItemVariantRow> variants = new LinkedHashMap<>();
    private final Map<String, ItemStack> stacks = new LinkedHashMap<>();
    private final List<ItemToolClassRow> toolClasses = new ArrayList<>();
    private final Map<String, ItemListEntryRow> listEntries = new LinkedHashMap<>();

    public ItemDomainStore(String datasetId) {
        this.datasetId = datasetId;
    }

    public String datasetId() {
        return datasetId;
    }

    public boolean containsItem(String itemId) {
        return items.containsKey(itemId);
    }

    public void putItem(String itemId, ItemRow row) {
        items.put(itemId, row);
    }

    public boolean containsVariant(String itemVariantId) {
        return variants.containsKey(itemVariantId);
    }

    public ItemVariantRow getVariant(String itemVariantId) {
        return variants.get(itemVariantId);
    }

    public ItemVariantRow putVariant(String itemVariantId, ItemVariantRow row, ItemStack stack) {
        variants.put(itemVariantId, row);
        stacks.put(itemVariantId, stack);
        return row;
    }

    public void addToolClass(ItemToolClassRow row) {
        toolClasses.add(row);
    }

    public void addListEntry(String itemVariantId, int listIndex) {
        if (!listEntries.containsKey(itemVariantId)) {
            listEntries.put(itemVariantId, new ItemListEntryRow(datasetId, itemVariantId, listIndex));
        }
    }

    public List<ItemVariantRow> variants() {
        return Collections.unmodifiableList(new ArrayList<>(variants.values()));
    }

    public Map<String, ItemStack> stacks() {
        return Collections.unmodifiableMap(stacks);
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
