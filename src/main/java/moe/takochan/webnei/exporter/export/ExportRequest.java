package moe.takochan.webnei.exporter.export;

/**
 * 一次导出请求。
 *
 * <p>request 只选择 plan，不直接指定 workflow 或 step。
 * 具体 step 列表由 plan registry 解析，workflow 只执行解析后的 plan。
 */
public final class ExportRequest {

    private final String planId;

    private ExportRequest(String planId) {
        this.planId = planId;
    }

    public static ExportRequest plan(String planId) {
        return new ExportRequest(planId);
    }

    public String planId() {
        return planId;
    }
}
