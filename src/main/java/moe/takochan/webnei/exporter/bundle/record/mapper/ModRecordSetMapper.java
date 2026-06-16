package moe.takochan.webnei.exporter.bundle.record.mapper;

import java.util.Arrays;
import java.util.List;

import moe.takochan.webnei.exporter.bundle.record.BundleRecordSet;
import moe.takochan.webnei.exporter.bundle.record.BundleRecordSetSpec;
import moe.takochan.webnei.exporter.bundle.record.IBundleRecordSetMapper;
import moe.takochan.webnei.exporter.domain.mod.ModExportModel;
import moe.takochan.webnei.exporter.domain.mod.model.ModRow;

/** mod export model 的记录集映射。 */
public final class ModRecordSetMapper implements IBundleRecordSetMapper<ModExportModel> {

    private static final BundleRecordSetSpec<ModRow> MOD = BundleRecordSetSpec.<ModRow>recordSet("mod")
        .field("dataset_id", ModRow::getDatasetId)
        .field("mod_id", ModRow::getModId)
        .field("name", ModRow::getName)
        .field("version", ModRow::getVersion)
        .field("source_type", ModRow::getSourceType)
        .field("source_file_name", ModRow::getSourceFileName)
        .field("source_sha256", ModRow::getSourceSha256)
        .field("enabled", ModRow::isEnabled);

    @Override
    public Class<ModExportModel> modelType() {
        return ModExportModel.class;
    }

    @Override
    public List<BundleRecordSet> recordSets(ModExportModel model) {
        return Arrays.asList(MOD.records(model.getMods()));
    }
}
