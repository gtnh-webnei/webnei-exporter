package moe.takochan.webnei.exporter.engine.plan;

import java.util.List;

import moe.takochan.webnei.exporter.engine.task.IExportTask;

/**
 * 一次导出计划。
 *
 * <p>
 * plan 只描述“这次导出由哪些 task 组成，以及执行顺序是什么”。
 * 它不负责执行 task，也不关心 task 内部如何从 NEI、mod registry 或渲染器取数据。
 */
public interface IExportPlan {

    /** 计划 ID，由命令请求并由 registry 解析。 */
    String id();

    /** 当前计划的展示文案 key，用于 chat / future GUI 显示。 */
    String labelKey();

    /** 本计划需要执行的数据域任务，顺序即执行顺序。 */
    List<IExportTask> tasks();
}
