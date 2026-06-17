package moe.takochan.webnei.exporter.domain.fluid.store;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.fluid.internal.FluidDomainData;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/**
 * fluid domain store — fluid domain 的跨 domain 交互边界。
 *
 * <p>
 * 当前只建立 domain 边界和空模型输出；具体注册/解析 API 等采集逻辑确定后再补充。
 */
public final class FluidDomainStore implements IDomainStore {

    private final FluidDomainData data;

    public FluidDomainStore(String datasetId) {
        this.data = new FluidDomainData(datasetId);
    }

    /** 当前 store 所属 dataset ID。 */
    public String datasetId() {
        return data.datasetId();
    }

    @Override
    public IExportModel toExportModel() {
        return data.toExportModel();
    }
}
