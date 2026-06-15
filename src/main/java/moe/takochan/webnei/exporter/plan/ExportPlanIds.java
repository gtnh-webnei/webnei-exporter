package moe.takochan.webnei.exporter.plan;

/** 导出计划 ID 集中定义，避免 command 层引用具体 step 或 workflow。 */
public final class ExportPlanIds {

    /** 当前对外使用的完整导出计划。 */
    public static final String ALL = "all";

    private ExportPlanIds() {}
}
