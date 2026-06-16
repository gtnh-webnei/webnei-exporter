package moe.takochan.webnei.exporter.bundle.record.mapper;

import java.util.Arrays;
import java.util.List;

import moe.takochan.webnei.exporter.bundle.record.BundleRecordSet;
import moe.takochan.webnei.exporter.bundle.record.BundleRecordSetSpec;
import moe.takochan.webnei.exporter.bundle.record.IBundleRecordSetMapper;
import moe.takochan.webnei.exporter.domain.item.ItemExportModel;
import moe.takochan.webnei.exporter.domain.item.model.ItemListEntryRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemToolClassRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;

/** item export model 的记录集映射。 */
public final class ItemRecordSetMapper implements IBundleRecordSetMapper<ItemExportModel> {

    private static final BundleRecordSetSpec<ItemRow> ITEM = BundleRecordSetSpec.<ItemRow>recordSet("item")
        .field("dataset_id", ItemRow::getDatasetId)
        .field("item_id", ItemRow::getItemId)
        .field("mod_id", ItemRow::getModId)
        .field("registry_name", ItemRow::getRegistryName)
        .field("unlocalized_name", ItemRow::getUnlocalizedName)
        .field("max_stack_size", ItemRow::getMaxStackSize)
        .field("max_damage", ItemRow::getMaxDamage)
        .field("runtime_item_id", ItemRow::getRuntimeItemId);

    private static final BundleRecordSetSpec<ItemVariantRow> ITEM_VARIANT = BundleRecordSetSpec
        .<ItemVariantRow>recordSet("item_variant")
        .field("dataset_id", ItemVariantRow::getDatasetId)
        .field("item_variant_id", ItemVariantRow::getItemVariantId)
        .field("item_id", ItemVariantRow::getItemId)
        .field("damage", ItemVariantRow::getDamage)
        .field("nbt_hash", ItemVariantRow::getNbtHash)
        .field("nbt_text", ItemVariantRow::getNbtText)
        .field("display_name", ItemVariantRow::getDisplayName)
        .field("tooltip_text", ItemVariantRow::getTooltipText)
        .field("chemical_expression", ItemVariantRow::getChemicalExpression);

    private static final BundleRecordSetSpec<ItemToolClassRow> ITEM_TOOL_CLASS = BundleRecordSetSpec
        .<ItemToolClassRow>recordSet("item_tool_class")
        .field("dataset_id", ItemToolClassRow::getDatasetId)
        .field("item_variant_id", ItemToolClassRow::getItemVariantId)
        .field("tool_class", ItemToolClassRow::getToolClass)
        .field("harvest_level", ItemToolClassRow::getHarvestLevel);

    private static final BundleRecordSetSpec<ItemListEntryRow> ITEM_LIST_ENTRY = BundleRecordSetSpec
        .<ItemListEntryRow>recordSet("item_list_entry")
        .field("dataset_id", ItemListEntryRow::getDatasetId)
        .field("item_variant_id", ItemListEntryRow::getItemVariantId)
        .field("list_index", ItemListEntryRow::getListIndex);

    @Override
    public Class<ItemExportModel> modelType() {
        return ItemExportModel.class;
    }

    @Override
    public List<BundleRecordSet> recordSets(ItemExportModel model) {
        return Arrays.asList(
            ITEM.records(model.getItems()),
            ITEM_VARIANT.records(model.getVariants()),
            ITEM_TOOL_CLASS.records(model.getToolClasses()),
            ITEM_LIST_ENTRY.records(model.getListEntries()));
    }
}
