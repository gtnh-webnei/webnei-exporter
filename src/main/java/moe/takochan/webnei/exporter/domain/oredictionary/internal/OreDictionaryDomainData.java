package moe.takochan.webnei.exporter.domain.oredictionary.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.oredictionary.OreDictionaryExportModel;
import moe.takochan.webnei.exporter.domain.oredictionary.model.OreDictionaryEntryRow;
import moe.takochan.webnei.exporter.domain.oredictionary.model.OreDictionaryRow;
import moe.takochan.webnei.exporter.engine.store.IDomainData;

/**
 * ore_dictionary domain store 的内部结果集。
 *
 * <p>
 * 该类持有 dictionary/entry 去重 key 和 list_index 分配状态；外部 domain 不应直接依赖这些实现细节。
 */
public final class OreDictionaryDomainData implements IDomainData {

    private final String datasetId;
    private final Map<String, OreDictionaryRow> dictionaries = new LinkedHashMap<>();
    private final Map<String, OreDictionaryEntryRow> entries = new LinkedHashMap<>();
    private final Map<String, Integer> nextListIndexByDictionaryName = new LinkedHashMap<>();

    public OreDictionaryDomainData(String datasetId) {
        this.datasetId = datasetId;
    }

    void putDictionary(String dictionaryName) {
        dictionaries.putIfAbsent(dictionaryName, new OreDictionaryRow(datasetId, dictionaryName));
    }

    void putEntry(String dictionaryName, String itemVariantId) {
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
