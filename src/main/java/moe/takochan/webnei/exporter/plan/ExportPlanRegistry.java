package moe.takochan.webnei.exporter.plan;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import moe.takochan.webnei.exporter.step.HandlerExportStep;
import moe.takochan.webnei.exporter.step.IExportStep;
import moe.takochan.webnei.exporter.step.RecipeVisualFactsExportStep;

/**
 * 导出计划注册表。
 *
 * <p>command 只传 plan id；这里负责把 plan id 映射成具体计划。
 * workflow 不从这里挑选 step，它只执行已经解析好的 plan。
 */
public final class ExportPlanRegistry {

    private final Map<String, IExportPlan> plans;

    public ExportPlanRegistry(Iterable<IExportPlan> plans) {
        Map<String, IExportPlan> byId = new LinkedHashMap<>();
        for (IExportPlan plan : plans) {
            byId.put(plan.id(), plan);
        }
        this.plans = Collections.unmodifiableMap(byId);
    }

    /** 当前默认注册的验证计划。正式全量导出计划后续再加入。 */
    public static ExportPlanRegistry defaults() {
        return new ExportPlanRegistry(
            Arrays.<IExportPlan>asList(
                new StaticExportPlan(
                    ExportPlanIds.HANDLER_DISCOVERY_VALIDATION,
                    "webnei.task.handlers",
                    Arrays.<IExportStep>asList(new HandlerExportStep())),
                new StaticExportPlan(
                    ExportPlanIds.RECIPE_VISUAL_FACTS_VALIDATION,
                    "webnei.task.slots",
                    Arrays.<IExportStep>asList(new HandlerExportStep(), new RecipeVisualFactsExportStep()))));
    }

    public IExportPlan get(String id) {
        return plans.get(id);
    }
}
