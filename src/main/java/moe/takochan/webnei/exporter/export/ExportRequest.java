package moe.takochan.webnei.exporter.export;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 一次导出请求。
 *
 * <p>
 * request 只选择 plan，并携带命令输入的少量参数；不直接指定 workflow 或 step。
 * 具体 step 列表由 plan registry 解析，workflow 只执行解析后的 plan。
 */
public final class ExportRequest {

    private final String planId;
    private final Map<String, String> options;

    private ExportRequest(String planId, Map<String, String> options) {
        this.planId = planId;
        this.options = Collections.unmodifiableMap(new LinkedHashMap<>(options));
    }

    public static ExportRequest plan(String planId) {
        return new ExportRequest(planId, Collections.<String, String>emptyMap());
    }

    public static ExportRequest plan(String planId, Map<String, String> options) {
        return new ExportRequest(planId, options);
    }

    public String planId() {
        return planId;
    }

    public String option(String key) {
        String value = options.get(key);
        return value == null ? "" : value;
    }
}
