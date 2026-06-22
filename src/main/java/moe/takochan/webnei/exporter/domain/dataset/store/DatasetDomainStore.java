package moe.takochan.webnei.exporter.domain.dataset.store;

import moe.takochan.webnei.exporter.domain.dataset.internal.DatasetDomainData;
import moe.takochan.webnei.exporter.domain.dataset.internal.DatasetRegistrar;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/**
 * dataset domain store。
 *
 * <p>
 * 跨 domain 只通过该 store 获取 dataset 信息；具体 row 构造逻辑在 internal 中维护。
 */
public final class DatasetDomainStore implements IDomainStore<DatasetDomainData, DatasetRegistrar> {

    private final DatasetDomainData data;
    private final DatasetRegistrar registrar;

    public DatasetDomainStore(DatasetDomainData data, DatasetRegistrar registrar) {
        this.data = data;
        this.registrar = registrar;
    }

    @Override
    public DatasetDomainData data() {
        return data;
    }

    @Override
    public DatasetRegistrar registrar() {
        return registrar;
    }
}
