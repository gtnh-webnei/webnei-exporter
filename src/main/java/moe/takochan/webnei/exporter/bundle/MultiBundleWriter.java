package moe.takochan.webnei.exporter.bundle;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import moe.takochan.webnei.exporter.domain.ExportModelSet;

/** 一次领域采集后顺序写出多个 bundle format。 */
public final class MultiBundleWriter implements IBundleWriter {

    private final List<IBundleWriter> writers;
    private final AssetBundlePreparer assetBundlePreparer;

    public MultiBundleWriter(List<IBundleWriter> writers) {
        this(writers, new AssetBundlePreparer());
    }

    public MultiBundleWriter(List<IBundleWriter> writers, AssetBundlePreparer assetBundlePreparer) {
        if (writers == null || writers.isEmpty()) {
            throw new IllegalArgumentException("writers must not be empty");
        }
        this.writers = Collections.unmodifiableList(new ArrayList<>(writers));
        this.assetBundlePreparer = assetBundlePreparer;
    }

    @Override
    public BundleFormat format() {
        return writers.get(0)
            .format();
    }

    @Override
    public BundleResult write(ExportModelSet models, BundleTarget target, BundleContext context)
        throws BundleException {
        ExportModelSet preparedModels = prepareAssetsOnce(models, target, context);
        List<String> files = new ArrayList<>();
        for (IBundleWriter writer : writers) {
            BundleResult result = writer.write(preparedModels, target, context);
            if (!result.isSuccess()) {
                return BundleResult.failure(writer.format(), result.getErrorMessage());
            }
            files.addAll(result.getOutputFiles());
        }
        return BundleResult.success(format(), files);
    }

    private ExportModelSet prepareAssetsOnce(ExportModelSet models, BundleTarget target, BundleContext context)
        throws BundleException {
        File outputDirectory = new File(target.getOutputDirectory(), BundleOutputPath.forModels(models));
        if (!outputDirectory.isDirectory() && !outputDirectory.mkdirs()) {
            throw new BundleException("Unable to create bundle directory: " + outputDirectory.getAbsolutePath());
        }
        return assetBundlePreparer.prepare(models, outputDirectory, context);
    }
}
