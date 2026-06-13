package moe.takochan.webnei.exporter.bundle;

import java.util.Collections;
import java.util.List;

public final class BundleResult {

    public final boolean success;
    public final BundleFormat format;
    public final List<String> outputFiles;
    public final String errorMessage;

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
