package moe.takochan.webnei.exporter.bundle;

import java.io.File;

public final class BundleTarget {

    public final File outputDirectory;

    private BundleTarget(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public static BundleTarget directory(File outputDirectory) {
        return new BundleTarget(outputDirectory);
    }
}
