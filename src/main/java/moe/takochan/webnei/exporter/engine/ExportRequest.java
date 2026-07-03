package moe.takochan.webnei.exporter.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import moe.takochan.webnei.exporter.bundle.BundleFormat;

/**
 * 一次导出请求。
 *
 * <p>
 * request 选择 plan 和 bundle format，并携带命令输入的少量参数；不直接指定 executor 或 task。
 * 具体 task 列表由 plan registry 解析，executor 只执行解析后的 plan。
 */
public final class ExportRequest {

    /** 调用方选择的导出计划 ID。 */
    private final String planId;

    /** 调用方选择的 bundle 输出格式。 */
    private final List<BundleFormat> bundleFormats;

    /** 命令层传入的导出参数。 */
    private final Map<String, String> options;

    private ExportRequest(String planId, List<BundleFormat> bundleFormats, Map<String, String> options) {
        if (bundleFormats == null || bundleFormats.isEmpty()) {
            throw new IllegalArgumentException("bundleFormats must not be empty");
        }
        this.planId = planId;
        this.bundleFormats = Collections.unmodifiableList(new ArrayList<>(bundleFormats));
        this.options = Collections.unmodifiableMap(new LinkedHashMap<>(options));
    }

    public static ExportRequest bundle(String planId, BundleFormat bundleFormat) {
        return bundle(planId, bundleFormat, Collections.<String, String>emptyMap());
    }

    public static ExportRequest bundle(String planId, BundleFormat bundleFormat, Map<String, String> options) {
        List<BundleFormat> formats = new ArrayList<>();
        formats.add(bundleFormat);
        return new ExportRequest(planId, formats, options);
    }

    public static ExportRequest bundle(String planId, List<BundleFormat> bundleFormats, Map<String, String> options) {
        return new ExportRequest(planId, bundleFormats, options);
    }

    public String planId() {
        return planId;
    }

    public BundleFormat bundleFormat() {
        return bundleFormats.get(0);
    }

    public List<BundleFormat> bundleFormats() {
        return bundleFormats;
    }

    public String option(String key) {
        String value = options.get(key);
        return value == null ? "" : value;
    }
}
