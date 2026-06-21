package moe.takochan.webnei.exporter.domain.oredictionary.store;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.oredictionary.internal.OreDictionaryDomainData;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/**
 * ore_dictionary domain store — ore_dictionary domain 的跨 domain 交互边界。
 *
 * <p>
 * 该类保留对外可调用的注册 API，dictionary/entry 去重和排序状态由 OreDictionaryDomainData 维护。
 */
public final class OreDictionaryDomainStore implements IDomainStore {

    private final OreDictionaryDomainData data;

    public OreDictionaryDomainStore(OreDictionaryDomainData data) {
        this.data = data;
    }

    /** 注册 dictionary name。 */
    public void registerDictionary(String dictionaryName) {
        data.registerDictionary(dictionaryName);
    }

    /** 注册 dictionary name 与 item variant 的关联；重复关联保留第一次出现的位置。 */
    public void registerEntry(String dictionaryName, String itemVariantId) {
        data.registerEntry(dictionaryName, itemVariantId);
    }

    @Override
    public IExportModel toExportModel() {
        return data.toExportModel();
    }
}
