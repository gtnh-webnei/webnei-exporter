package moe.takochan.webnei.exporter.domain.fluid.internal;

import java.util.Collections;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.fluid.FluidExportModel;

/**
 * fluid domain store 的内部数据。
 *
 * <p>
 * 当前只承载 dataset_id 和空模型输出；fluid/container/block 的注册、去重和排序状态等实现细节后续随采集逻辑补充。
 */
public final class FluidDomainData {

    private final String datasetId;

    public FluidDomainData(String datasetId) {
        this.datasetId = datasetId;
    }

    public String datasetId() {
        return datasetId;
    }

    public IExportModel toExportModel() {
        return new FluidExportModel(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }
}
