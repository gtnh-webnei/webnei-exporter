package moe.takochan.webnei.exporter.domain.dataset.store;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.dataset.internal.DatasetDomainData;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/**
 * dataset domain store。
 *
 * <p>
 * 跨 domain 只通过该 store 获取 dataset 信息；具体 row 构造逻辑在 internal 中维护。
 */
public final class DatasetDomainStore implements IDomainStore {

    private final DatasetDomainData data = new DatasetDomainData();

    public void initialize(String packSlug, String packVersion, String variant, String language) {
        data.initialize(packSlug, packVersion, variant, language);
    }

    /**
     * 对外暴露 dataset_id，供后续 domain 建立外键和稳定 ID。
     *
     * <p>
     * 其他 domain 不应直接访问 DatasetRow；需要新增跨 domain 数据时，优先扩展本 store 的公开接口。
     */
    public String datasetId() {
        return data.datasetId();
    }

    @Override
    public IExportModel toExportModel() {
        return data.toExportModel();
    }
}
