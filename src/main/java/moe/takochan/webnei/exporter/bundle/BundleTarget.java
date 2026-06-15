package moe.takochan.webnei.exporter.bundle;

import java.io.File;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** bundle writer 的基础输出目标。 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BundleTarget {

    /** bundle writer 可在其下继续决定具体子目录和文件名的输出根目录。 */
    private final File outputDirectory;

    public static BundleTarget directory(File outputDirectory) {
        return new BundleTarget(outputDirectory);
    }
}
