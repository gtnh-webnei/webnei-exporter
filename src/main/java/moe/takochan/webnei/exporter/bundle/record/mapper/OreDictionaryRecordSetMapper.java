package moe.takochan.webnei.exporter.bundle.record.mapper;

import java.util.Arrays;
import java.util.List;

import moe.takochan.webnei.exporter.bundle.record.BundleRecordSet;
import moe.takochan.webnei.exporter.bundle.record.BundleRecordSetSpec;
import moe.takochan.webnei.exporter.bundle.record.IBundleRecordSetMapper;
import moe.takochan.webnei.exporter.domain.oredictionary.OreDictionaryExportModel;
import moe.takochan.webnei.exporter.domain.oredictionary.model.OreDictionaryEntryRow;
import moe.takochan.webnei.exporter.domain.oredictionary.model.OreDictionaryRow;

/** ore_dictionary export model 的记录集映射。 */
public final class OreDictionaryRecordSetMapper implements IBundleRecordSetMapper<OreDictionaryExportModel> {

    private static final BundleRecordSetSpec<OreDictionaryRow> ORE_DICTIONARY = BundleRecordSetSpec
        .<OreDictionaryRow>recordSet("ore_dictionary", 100)
        .field("dataset_id", OreDictionaryRow::getDatasetId)
        .field("dictionary_name", OreDictionaryRow::getDictionaryName);

    private static final BundleRecordSetSpec<OreDictionaryEntryRow> ORE_DICTIONARY_ENTRY = BundleRecordSetSpec
        .<OreDictionaryEntryRow>recordSet("ore_dictionary_entry", 110)
        .field("dataset_id", OreDictionaryEntryRow::getDatasetId)
        .field("dictionary_name", OreDictionaryEntryRow::getDictionaryName)
        .field("item_variant_id", OreDictionaryEntryRow::getItemVariantId)
        .field("list_index", OreDictionaryEntryRow::getListIndex);

    @Override
    public Class<OreDictionaryExportModel> modelType() {
        return OreDictionaryExportModel.class;
    }

    @Override
    public List<BundleRecordSet> recordSets(OreDictionaryExportModel model) {
        return Arrays
            .asList(ORE_DICTIONARY.records(model.getDictionaries()), ORE_DICTIONARY_ENTRY.records(model.getEntries()));
    }
}
