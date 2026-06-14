package moe.takochan.webnei.exporter.plan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import moe.takochan.webnei.exporter.step.IExportStep;

/**
 * 固定 step 列表的简单计划实现。
 *
 * <p>当前阶段用于把验证计划先落地；后续如果 plan 需要根据配置动态选择 step，
 * 可以新增专门的 plan 实现，而不需要改 workflow。
 */
final class StaticExportPlan implements IExportPlan {

    private final String id;
    private final String labelKey;
    private final List<IExportStep> steps;

    StaticExportPlan(String id, String labelKey, List<IExportStep> steps) {
        this.id = id;
        this.labelKey = labelKey;
        this.steps = Collections.unmodifiableList(new ArrayList<>(steps));
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
    public List<IExportStep> steps() {
        return steps;
    }
}
