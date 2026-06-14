package moe.takochan.webnei.exporter.plan;

/** 导出计划 ID 集中定义，避免 command 层引用具体 step 或 workflow。 */
public final class ExportPlanIds {

    /** 第一阶段验证：只导出 dataset / mod 基础数据。 */
    public static final String DATASET_MOD_VALIDATION = "dataset-mod";

    /** 临时验证：只导出 NEI handler/category 发现结果。 */
    public static final String HANDLER_DISCOVERY_VALIDATION = "handler-scan";

    /** 临时验证：导出 NEI final result 中的 recipe visual facts。 */
    public static final String RECIPE_VISUAL_FACTS_VALIDATION = "slot-extraction";

    private ExportPlanIds() {}
}
