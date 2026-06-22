package moe.takochan.webnei.exporter.domain.oredictionary.store;

import moe.takochan.webnei.exporter.domain.oredictionary.internal.OreDictionaryDomainData;
import moe.takochan.webnei.exporter.domain.oredictionary.internal.OreDictionaryRegistrar;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/**
 * ore_dictionary domain store — ore_dictionary domain 的跨 domain 交互边界。
 *
 * <p>
 * 该类保留对外可调用的注册 API，dictionary/entry 去重和排序状态由 OreDictionaryDomainData 维护。
 */
public final class OreDictionaryDomainStore implements IDomainStore<OreDictionaryDomainData, OreDictionaryRegistrar> {

    private final OreDictionaryDomainData data;
    private final OreDictionaryRegistrar registrar;

    public OreDictionaryDomainStore(OreDictionaryDomainData data, OreDictionaryRegistrar registrar) {
        this.data = data;
        this.registrar = registrar;
    }

    @Override
    public OreDictionaryDomainData data() {
        return data;
    }

    @Override
    public OreDictionaryRegistrar registrar() {
        return registrar;
    }
}
