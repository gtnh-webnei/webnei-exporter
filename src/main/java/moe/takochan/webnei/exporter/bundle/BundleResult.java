package moe.takochan.webnei.exporter.bundle;

import java.util.Collections;
import java.util.List;

import lombok.Getter;

/** bundle writer 执行结果。 */
@Getter
public final class BundleResult {

    /** bundle 是否写出成功。 */
    private final boolean success;

    /** 本次使用的 bundle 格式。 */
    private final BundleFormat format;

    /** 成功写出的文件路径列表。 */
    private final List<String> outputFiles;

    /** 失败原因；成功时为空。 */
    private final String errorMessage;

    private BundleResult(boolean success, BundleFormat format, List<String> outputFiles, String errorMessage) {
        this.success = success;
        this.format = format;
        this.outputFiles = Collections.unmodifiableList(outputFiles);
        this.errorMessage = errorMessage;
    }

    public static BundleResult success(BundleFormat format, List<String> outputFiles) {
        return new BundleResult(true, format, outputFiles, "");
    }

    public static BundleResult failure(BundleFormat format, String errorMessage) {
        return new BundleResult(false, format, Collections.<String>emptyList(), errorMessage);
    }

    public String outputSummary() {
        if (outputFiles.isEmpty()) {
            return "";
        }
        if (outputFiles.size() == 1) {
            return outputFiles.get(0);
        }
        return outputFiles.toString();
    }
}
