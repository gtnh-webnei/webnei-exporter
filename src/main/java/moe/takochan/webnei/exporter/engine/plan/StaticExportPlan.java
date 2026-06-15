package moe.takochan.webnei.exporter.engine.plan;

import java.util.Arrays;
import java.util.List;

import moe.takochan.webnei.exporter.engine.task.IExportTask;
import moe.takochan.webnei.exporter.export.ExportPlan;

/**
 * 固定 task 列表的简单计划实现。
 *
 * <p>
 * 当前阶段用于把导出计划先落地；后续如果 plan 需要根据配置动态选择 task，
 * 可以新增专门的 plan 实现，而不需要改 executor。
 */
public final class StaticExportPlan implements IExportPlan {

    private final String id;
    private final String labelKey;
    private final List<IExportTask> tasks;

    public StaticExportPlan(String labelKey, ExportPlan plan) {
        this.labelKey = labelKey;
        this.id = plan.getId();
        this.tasks = Arrays.asList(plan.getTasks());
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String labelKey() {
        return labelKey;
    }

    @Override
    public List<IExportTask> tasks() {
        return tasks;
    }
}
