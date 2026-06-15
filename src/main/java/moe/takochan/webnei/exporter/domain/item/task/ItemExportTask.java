package moe.takochan.webnei.exporter.domain.item.task;

import moe.takochan.webnei.exporter.domain.dataset.store.DatasetDomainStore;
import moe.takochan.webnei.exporter.domain.item.internal.ItemRegistrar;
import moe.takochan.webnei.exporter.domain.item.internal.NeiItemPanelSource;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;
import moe.takochan.webnei.exporter.engine.task.ExportTaskContext;
import moe.takochan.webnei.exporter.engine.task.IExportTask;

/**
 * item 数据域导出任务。
 *
 * <p>
 * 使用 NEI panel source 作为初始 ItemStack 种子，通过 ItemRegistrar 处理后存入 ItemDomainStore。
 * 依赖 DatasetDomainStore（需要 dataset_id）。
 */
public final class ItemExportTask implements IExportTask {

    public static final String ID = "item-export";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String labelKey() {
        return "webnei.task.items";
    }

    @Override
    public void execute(ExportTaskContext context) {
        String datasetId = context.store(DatasetDomainStore.class).row().getDatasetId();

        ItemDomainStore store = new ItemDomainStore(datasetId);
        ItemRegistrar registrar = new ItemRegistrar(store);
        new NeiItemPanelSource().collect(registrar, store);
        context.register(ItemDomainStore.class, store);
    }
}
