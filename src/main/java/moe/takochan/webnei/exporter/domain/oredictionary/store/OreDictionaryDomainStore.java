package moe.takochan.webnei.exporter.domain.oredictionary.store;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.oredictionary.OreDictionaryExportModel;
import moe.takochan.webnei.exporter.domain.oredictionary.model.OreDictionaryEntryRow;
import moe.takochan.webnei.exporter.domain.oredictionary.model.OreDictionaryRow;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/** ore_dictionary domain store — 保存 dictionary name 及其 item variant 关联。 */
public final class OreDictionaryDomainStore implements IDomainStore {

    private final String datasetId;
    private final Map<String, OreDictionaryRow> dictionaries = new LinkedHashMap<>();
    private final Map<String, OreDictionaryEntryRow> entries = new LinkedHashMap<>();
    private final Map<String, Integer> nextListIndexByDictionaryName = new LinkedHashMap<>();

    public OreDictionaryDomainStore(String datasetId) {
        this.datasetId = datasetId;
    }

    /** 注册 dictionary name。 */
    public void addDictionary(String dictionaryName) {
        dictionaries.putIfAbsent(dictionaryName, new OreDictionaryRow(datasetId, dictionaryName));
    }

    /** 注册 dictionary name 与 item variant 的关联；重复关联保留第一次出现的位置。 */
    public void addEntry(String dictionaryName, String itemVariantId) {
        String key = dictionaryName + '\u0000' + itemVariantId;
        if (entries.containsKey(key)) {
            return;
        }
        int listIndex = nextListIndexByDictionaryName.getOrDefault(dictionaryName, 0);
        nextListIndexByDictionaryName.put(dictionaryName, listIndex + 1);
        entries.put(key, new OreDictionaryEntryRow(datasetId, dictionaryName, itemVariantId, listIndex));
    }

    @Override
    public IExportModel toExportModel() {
        return new OreDictionaryExportModel(new ArrayList<>(dictionaries.values()), new ArrayList<>(entries.values()));
    }
}
