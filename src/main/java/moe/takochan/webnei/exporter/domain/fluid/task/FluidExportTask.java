package moe.takochan.webnei.exporter.domain.fluid.task;

import moe.takochan.webnei.exporter.domain.dataset.store.DatasetDomainStore;
import moe.takochan.webnei.exporter.domain.fluid.internal.ForgeFluidRegistrySource;
import moe.takochan.webnei.exporter.domain.fluid.store.FluidDomainStore;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;
import moe.takochan.webnei.exporter.engine.task.ExportTaskContext;
import moe.takochan.webnei.exporter.engine.task.IExportTask;

/**
 * fluid 数据域导出任务。
 *
 * <p>
 * 使用 Forge fluid registry source 作为流体种子，写入 FluidDomainStore；方块和容器关系在注册流体时自动挂接。
 * 依赖 DatasetDomainStore（需要 dataset_id）和 ItemDomainStore（解析方块/容器 item variant）。
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
        ItemDomainStore itemStore = context.store(ItemDomainStore.class);

        FluidDomainStore store = new FluidDomainStore(datasetId, itemStore);
        new ForgeFluidRegistrySource().collect(store);
        context.register(FluidDomainStore.class, store);
    }
}
