package moe.takochan.webnei.exporter.plan;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import moe.takochan.webnei.exporter.step.DatasetModExportStep;
import moe.takochan.webnei.exporter.step.HandlerExportStep;
import moe.takochan.webnei.exporter.step.ItemExportStep;
import moe.takochan.webnei.exporter.step.RecipeVisualFactsExportStep;

/**
 * 导出计划注册表。
 *
 * <p>
 * command 只传 plan id；这里负责把 plan id 映射成具体计划。
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

    /** 当前默认注册的导出计划。 */
    public static ExportPlanRegistry defaults() {
        return new ExportPlanRegistry(
            Arrays.asList(
                new StaticExportPlan(
                    ExportPlanIds.ALL,
                    "webnei.task.all",
                    Arrays.asList(
                        new DatasetModExportStep(),
                        new ItemExportStep(),
                        new HandlerExportStep(),
                        new RecipeVisualFactsExportStep()))));
    }

    public IExportPlan get(String id) {
        return plans.get(id);
    }
}
