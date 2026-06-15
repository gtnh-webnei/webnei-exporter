package moe.takochan.webnei.exporter.domain.item.task;

import moe.takochan.webnei.exporter.domain.dataset.task.DatasetIdentity;
import moe.takochan.webnei.exporter.engine.task.ExportTaskContext;
import moe.takochan.webnei.exporter.engine.task.IExportTask;
import moe.takochan.webnei.exporter.domain.item.IItemPanelSource;
import moe.takochan.webnei.exporter.domain.item.ItemStackCatalog;
import moe.takochan.webnei.exporter.domain.item.ItemStackCatalogProvider;
import moe.takochan.webnei.exporter.domain.item.NeiItemPanelSource;

/**
 * item 数据域当前阶段的调度 task。
 *
 * <p>
 * 本 task 只调度 NEI panel source 作为初始 ItemStack 种子，并输出 catalog 中已登记的领域模型。真正的
 * item/item_variant/item_tool_class 采集逻辑在 ItemStackCatalog 及底层 collector/adapter 中。
 */
public final class ItemExportTask implements IExportTask {

    public static final String ID = "item-export";

    private final IItemPanelSource itemPanelSource;

    public ItemExportTask() {
        this(new NeiItemPanelSource());
    }

    ItemExportTask(IItemPanelSource itemPanelSource) {
        this.itemPanelSource = itemPanelSource;
    }

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
        DatasetIdentity dataset = DatasetIdentity.from(context);
        ItemStackCatalog catalog = ItemStackCatalogProvider.getOrCreate(context, dataset);
        itemPanelSource.collect(catalog);
        context.addModel(catalog.toModel());
    }
}
