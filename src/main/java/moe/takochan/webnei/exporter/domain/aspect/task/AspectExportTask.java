package moe.takochan.webnei.exporter.domain.aspect.task;

import moe.takochan.webnei.exporter.domain.aspect.internal.AspectDomainData;
import moe.takochan.webnei.exporter.domain.aspect.internal.AspectRegistrar;
import moe.takochan.webnei.exporter.domain.aspect.internal.ThaumcraftAspectSource;
import moe.takochan.webnei.exporter.domain.aspect.store.AspectDomainStore;
import moe.takochan.webnei.exporter.domain.dataset.store.DatasetDomainStore;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;
import moe.takochan.webnei.exporter.engine.task.ExportTaskContext;
import moe.takochan.webnei.exporter.engine.task.IExportTask;

/** aspect 数据域导出任务。 */
public final class AspectExportTask implements IExportTask {

    public static final String ID = "aspect-export";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String labelKey() {
        return "webnei.task.aspects";
    }

    @Override
    public void execute(ExportTaskContext context) {
        String datasetId = context.store(DatasetDomainStore.class)
            .data()
            .datasetId();
        ItemDomainStore itemStore = context.store(ItemDomainStore.class);

        AspectDomainData data = new AspectDomainData();
        AspectRegistrar registrar = new AspectRegistrar(data, datasetId);
        new ThaumcraftAspectSource(registrar, itemStore).collect();

        AspectDomainStore store = new AspectDomainStore(data, registrar);
        context.register(AspectDomainStore.class, store);
    }
}
