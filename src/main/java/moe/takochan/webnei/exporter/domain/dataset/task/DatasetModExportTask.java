package moe.takochan.webnei.exporter.domain.dataset.task;

import moe.takochan.webnei.exporter.domain.dataset.internal.DatasetDomainData;
import moe.takochan.webnei.exporter.domain.dataset.internal.DatasetRegistrar;
import moe.takochan.webnei.exporter.domain.dataset.internal.DatasetRequestSource;
import moe.takochan.webnei.exporter.domain.dataset.store.DatasetDomainStore;
import moe.takochan.webnei.exporter.engine.task.ExportTaskContext;
import moe.takochan.webnei.exporter.engine.task.IExportTask;

/**
 * dataset 数据域导出任务。
 *
 * <p>
 * 从请求参数构建 dataset 信息并注册 DatasetDomainStore，供后续所有 domain 获取 dataset_id。
 */
public final class DatasetModExportTask implements IExportTask {

    public static final String ID = "dataset-export";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String labelKey() {
        return "webnei.task.dataset";
    }

    @Override
    public void execute(ExportTaskContext context) {
        DatasetDomainData data = new DatasetDomainData();
        DatasetDomainStore store = new DatasetDomainStore(data);
        DatasetRegistrar registrar = new DatasetRegistrar(data);
        new DatasetRequestSource(registrar, context).collect();
        context.register(DatasetDomainStore.class, store);
    }
}
