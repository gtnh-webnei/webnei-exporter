package moe.takochan.webnei.exporter.bundle.record.mapper;

import java.util.Arrays;
import java.util.List;

import moe.takochan.webnei.exporter.bundle.record.BundleRecordSet;
import moe.takochan.webnei.exporter.bundle.record.BundleRecordSetSpec;
import moe.takochan.webnei.exporter.bundle.record.IBundleRecordSetMapper;
import moe.takochan.webnei.exporter.domain.dataset.DatasetExportModel;
import moe.takochan.webnei.exporter.domain.dataset.model.DatasetRow;

/** dataset export model 的记录集映射。 */
public final class DatasetRecordSetMapper implements IBundleRecordSetMapper<DatasetExportModel> {

    private static final BundleRecordSetSpec<DatasetRow> DATASET = BundleRecordSetSpec
        .<DatasetRow>recordSet("dataset", 10)
        .field("dataset_id", DatasetRow::getDatasetId)
        .field("pack_slug", DatasetRow::getPackSlug)
        .field("pack_version", DatasetRow::getPackVersion)
        .field("variant", DatasetRow::getVariant)
        .field("language", DatasetRow::getLanguage)
        .field("display_name", DatasetRow::getDisplayName)
        .field("schema_version", DatasetRow::getSchemaVersion)
        .field("exporter_version", DatasetRow::getExporterVersion)
        .field("created_at", DatasetRow::getCreatedAt)
        .field("minecraft_version", DatasetRow::getMinecraftVersion);

    @Override
    public Class<DatasetExportModel> modelType() {
        return DatasetExportModel.class;
    }

    @Override
    public List<BundleRecordSet> recordSets(DatasetExportModel model) {
        return Arrays.asList(DATASET.records(model.getDataset()));
    }
}
