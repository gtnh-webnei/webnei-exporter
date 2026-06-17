package moe.takochan.webnei.exporter.domain.fluid.task;

import moe.takochan.webnei.exporter.domain.dataset.store.DatasetDomainStore;
import moe.takochan.webnei.exporter.domain.fluid.store.FluidDomainStore;
import moe.takochan.webnei.exporter.engine.task.ExportTaskContext;
import moe.takochan.webnei.exporter.engine.task.IExportTask;

/**
 * fluid 数据域导出任务。
 *
 * <p>
 * 当前仅注册空的 FluidDomainStore，后续再补充 Forge fluid 采集、容器关联和方块关联逻辑。
 */
public final class FluidExportTask implements IExportTask {

    public static final String ID = "fluid-export";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String labelKey() {
        return "webnei.task.fluids";
    }

    @Override
    public void execute(ExportTaskContext context) {
        String datasetId = context.store(DatasetDomainStore.class)
            .datasetId();
        context.register(FluidDomainStore.class, new FluidDomainStore(datasetId));
    }
}
