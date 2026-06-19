package moe.takochan.webnei.exporter.export;

import lombok.Getter;
import moe.takochan.webnei.exporter.domain.asset.task.AssetExportTask;
import moe.takochan.webnei.exporter.domain.dataset.task.DatasetModExportTask;
import moe.takochan.webnei.exporter.domain.fluid.task.FluidExportTask;
import moe.takochan.webnei.exporter.domain.item.task.ItemExportTask;
import moe.takochan.webnei.exporter.domain.mod.task.ModExportTask;
import moe.takochan.webnei.exporter.domain.oredictionary.task.OreDictionaryExportTask;
import moe.takochan.webnei.exporter.engine.task.IExportTask;

/**
 * 导出计划 ID 集中定义，避免 command 层引用具体 task 或 executor。
 */
@Getter
public enum ExportPlan {

    // spotless:off
    ALL("all",
        new DatasetModExportTask(),
        new ModExportTask(),
        new ItemExportTask(),
        new FluidExportTask(),
        new OreDictionaryExportTask(),
        new AssetExportTask()
    );
    // spotless:on

    private final String id;
    private final IExportTask[] tasks;

    ExportPlan(String id, IExportTask... tasks) {
        this.id = id;
        this.tasks = tasks;
    }
}
