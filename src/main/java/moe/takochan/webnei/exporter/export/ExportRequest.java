package moe.takochan.webnei.exporter.export;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import moe.takochan.webnei.exporter.bundle.BundleFormat;

/**
 * 一次导出请求。
 *
 * <p>
 * request 选择 plan 和 bundle format，并携带命令输入的少量参数；不直接指定 workflow 或 step。
 * 具体 step 列表由 plan registry 解析，workflow 只执行解析后的 plan。
 */
public final class ExportRequest {

    /** 调用方选择的导出计划 ID。 */
    private final String planId;

    /** 调用方选择的 bundle 输出格式。 */
    private final BundleFormat bundleFormat;

    /** 命令层传入的导出参数。 */
    private final Map<String, String> options;

    private ExportRequest(String planId, BundleFormat bundleFormat, Map<String, String> options) {
        this.planId = planId;
        this.bundleFormat = bundleFormat;
        this.options = Collections.unmodifiableMap(new LinkedHashMap<>(options));
    }

    public static ExportRequest bundle(String planId, BundleFormat bundleFormat) {
        return new ExportRequest(planId, bundleFormat, Collections.<String, String>emptyMap());
    }

    public static ExportRequest bundle(String planId, BundleFormat bundleFormat, Map<String, String> options) {
        return new ExportRequest(planId, bundleFormat, options);
    }

    public String planId() {
        return planId;
    }

    public BundleFormat bundleFormat() {
        return bundleFormat;
    }

    public String option(String key) {
        String value = options.get(key);
        return value == null ? "" : value;
    }
}
