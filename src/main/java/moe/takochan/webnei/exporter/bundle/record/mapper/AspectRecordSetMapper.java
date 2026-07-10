package moe.takochan.webnei.exporter.bundle.record.mapper;

import java.util.Arrays;
import java.util.List;

import moe.takochan.webnei.exporter.bundle.record.BundleRecordSet;
import moe.takochan.webnei.exporter.bundle.record.BundleRecordSetSpec;
import moe.takochan.webnei.exporter.bundle.record.IBundleRecordSetMapper;
import moe.takochan.webnei.exporter.domain.aspect.AspectExportModel;
import moe.takochan.webnei.exporter.domain.aspect.model.AspectComponentRow;
import moe.takochan.webnei.exporter.domain.aspect.model.AspectItemRow;
import moe.takochan.webnei.exporter.domain.aspect.model.AspectRow;

/** aspect export model 的记录集映射。 */
public final class AspectRecordSetMapper implements IBundleRecordSetMapper<AspectExportModel> {

    private static final BundleRecordSetSpec<AspectRow> ASPECT = BundleRecordSetSpec.<AspectRow>recordSet("aspect", 150)
        .field("dataset_id", AspectRow::getDatasetId)
        .field("aspect_id", AspectRow::getAspectId)
        .field("item_variant_id", AspectRow::getItemVariantId)
        .field("display_name", AspectRow::getDisplayName)
        .field("description", AspectRow::getDescription)
        .field("primal", AspectRow::isPrimal)
        .field("color", AspectRow::getColor)
        .field("blend", AspectRow::getBlend)
        .field("chat_color", AspectRow::getChatColor)
        .field("registry_order", AspectRow::getRegistryOrder);

    private static final BundleRecordSetSpec<AspectComponentRow> ASPECT_COMPONENT = BundleRecordSetSpec
        .<AspectComponentRow>recordSet("aspect_component", 160)
        .field("dataset_id", AspectComponentRow::getDatasetId)
        .field("aspect_id", AspectComponentRow::getAspectId)
        .field("component_index", AspectComponentRow::getComponentIndex)
        .field("component_aspect_id", AspectComponentRow::getComponentAspectId);

    private static final BundleRecordSetSpec<AspectItemRow> ASPECT_ITEM = BundleRecordSetSpec
        .<AspectItemRow>recordSet("aspect_item", 170)
        .field("dataset_id", AspectItemRow::getDatasetId)
        .field("item_variant_id", AspectItemRow::getItemVariantId)
        .field("aspect_id", AspectItemRow::getAspectId)
        .field("amount", AspectItemRow::getAmount);

    @Override
    public Class<AspectExportModel> modelType() {
        return AspectExportModel.class;
    }

    @Override
    public List<BundleRecordSet> recordSets(AspectExportModel model) {
        return Arrays.asList(
            ASPECT.records(model.getAspects()),
            ASPECT_COMPONENT.records(model.getComponents()),
            ASPECT_ITEM.records(model.getItemAspects()));
    }
}
