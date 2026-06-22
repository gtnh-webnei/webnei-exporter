package moe.takochan.webnei.exporter.domain.mod.task;

import moe.takochan.webnei.exporter.domain.dataset.store.DatasetDomainStore;
import moe.takochan.webnei.exporter.domain.mod.internal.ForgeModSource;
import moe.takochan.webnei.exporter.domain.mod.internal.ModDomainData;
import moe.takochan.webnei.exporter.domain.mod.internal.ModRegistrar;
import moe.takochan.webnei.exporter.domain.mod.store.ModDomainStore;
import moe.takochan.webnei.exporter.engine.task.ExportTaskContext;
import moe.takochan.webnei.exporter.engine.task.IExportTask;

/**
 * mod 数据域导出任务。
 *
 * <p>
 * 扫描当前 Forge 已加载的 mod 列表，通过 ModRegistrar 处理后存入 ModDomainStore。
 * 依赖 DatasetDomainStore（需要 dataset_id）。
 */
public final class ModExportTask implements IExportTask {

    public static final String ID = "mod-export";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String labelKey() {
        return "webnei.task.mods";
    }

    @Override
    public void execute(ExportTaskContext context) {
        String datasetId = context.store(DatasetDomainStore.class)
            .data()
            .datasetId();

        ModDomainData data = new ModDomainData();
        ModRegistrar registrar = new ModRegistrar(data, datasetId);
        ModDomainStore store = new ModDomainStore(data, registrar);

        new ForgeModSource(registrar).collect();
        context.register(ModDomainStore.class, store);
    }
}
