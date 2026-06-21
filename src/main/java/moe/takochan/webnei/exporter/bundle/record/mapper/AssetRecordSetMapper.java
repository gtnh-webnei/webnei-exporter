package moe.takochan.webnei.exporter.bundle.record.mapper;

import java.util.Collections;
import java.util.List;

import moe.takochan.webnei.exporter.bundle.record.BundleRecordSet;
import moe.takochan.webnei.exporter.bundle.record.BundleRecordSetSpec;
import moe.takochan.webnei.exporter.bundle.record.IBundleRecordSetMapper;
import moe.takochan.webnei.exporter.domain.asset.AssetExportModel;
import moe.takochan.webnei.exporter.domain.asset.model.AssetRow;

/** asset export model 的记录集映射。 */
public final class AssetRecordSetMapper implements IBundleRecordSetMapper<AssetExportModel> {

    private static final BundleRecordSetSpec<AssetRow> ASSET = BundleRecordSetSpec
        .<AssetRow>recordSet(AssetExportModel.TYPE)
        .field("dataset_id", AssetRow::getDatasetId)
        .field("owner_type", AssetRow::getOwnerType)
        .field("owner_id", AssetRow::getOwnerId)
        .field("kind", AssetRow::getKind)
        .field("path", AssetRow::getPath)
        .field("media_type", AssetRow::getMediaType)
        .field("width", AssetRow::getWidth)
        .field("height", AssetRow::getHeight)
        .field("metadata_json", AssetRow::getMetadataJson);

    @Override
    public Class<AssetExportModel> modelType() {
        return AssetExportModel.class;
    }

    @Override
    public List<BundleRecordSet> recordSets(AssetExportModel model) {
        return Collections.singletonList(ASSET.records(model.getAssets()));
    }
}
