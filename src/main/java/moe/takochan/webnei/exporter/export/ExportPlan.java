package moe.takochan.webnei.exporter.export;

import lombok.Getter;
import moe.takochan.webnei.exporter.engine.task.IExportTask;
import moe.takochan.webnei.exporter.domain.dataset.task.DatasetModExportTask;
import moe.takochan.webnei.exporter.domain.item.task.ItemExportTask;

/**
 * 导出计划 ID 集中定义，避免 command 层引用具体 task 或 executor。
 */
@Getter
public enum ExportPlan {

    ALL("all", new DatasetModExportTask(), new ItemExportTask());

    private final String id;
    private final IExportTask[] tasks;

    ExportPlan(String id, IExportTask... tasks) {
        this.id = id;
        this.tasks = tasks;
    }
}
