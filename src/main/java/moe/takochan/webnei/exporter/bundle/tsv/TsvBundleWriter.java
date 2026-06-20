package moe.takochan.webnei.exporter.bundle.tsv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import moe.takochan.webnei.exporter.bundle.AssetBundlePreparer;
import moe.takochan.webnei.exporter.bundle.BundleContext;
import moe.takochan.webnei.exporter.bundle.BundleException;
import moe.takochan.webnei.exporter.bundle.BundleFormat;
import moe.takochan.webnei.exporter.bundle.BundleOutputPath;
import moe.takochan.webnei.exporter.bundle.BundleResult;
import moe.takochan.webnei.exporter.bundle.BundleTarget;
import moe.takochan.webnei.exporter.bundle.IBundleWriter;
import moe.takochan.webnei.exporter.bundle.record.BundleRecordSet;
import moe.takochan.webnei.exporter.bundle.record.BundleRecordSetMapperRegistry;
import moe.takochan.webnei.exporter.bundle.record.IBundleRecordSetMapper;
import moe.takochan.webnei.exporter.domain.ExportModelSet;
import moe.takochan.webnei.exporter.domain.IExportModel;

/** 把领域中间模型写成 TSV bundle。 */
public final class TsvBundleWriter implements IBundleWriter {

    private final BundleRecordSetMapperRegistry mapperRegistry;
    private final AssetBundlePreparer assetBundlePreparer;

    public TsvBundleWriter() {
        this(BundleRecordSetMapperRegistry.defaults(), new AssetBundlePreparer());
    }

    public TsvBundleWriter(BundleRecordSetMapperRegistry mapperRegistry) {
        this(mapperRegistry, new AssetBundlePreparer());
    }

    public TsvBundleWriter(BundleRecordSetMapperRegistry mapperRegistry, AssetBundlePreparer assetBundlePreparer) {
        this.mapperRegistry = mapperRegistry;
        this.assetBundlePreparer = assetBundlePreparer;
    }

    @Override
    public BundleFormat format() {
        return BundleFormat.TSV;
    }

    @Override
    public BundleResult write(ExportModelSet models, BundleTarget target, BundleContext context)
        throws BundleException {
        File outputDirectory = new File(target.getOutputDirectory(), BundleOutputPath.forModels(models));
        if (!outputDirectory.isDirectory() && !outputDirectory.mkdirs()) {
            throw new BundleException("Unable to create bundle directory: " + outputDirectory.getAbsolutePath());
        }

        ExportModelSet preparedModels = assetBundlePreparer.prepare(models, outputDirectory);
        List<String> files = new ArrayList<>();
        for (BundleRecordSet recordSet : recordSets(preparedModels.getModels())) {
            File file = new File(outputDirectory, recordSet.getName() + ".tsv");
            writeRecordSet(recordSet, file);
            files.add(file.getAbsolutePath());
        }
        return BundleResult.success(format(), files);
    }

    /** 将所有 model 映射成 bundle record set。 */
    private List<BundleRecordSet> recordSets(List<IExportModel> models) throws BundleException {
        List<BundleRecordSet> recordSets = new ArrayList<>();
        for (IExportModel model : models) {
            IBundleRecordSetMapper<?> mapper = mapperRegistry.mapperFor(model);
            if (mapper == null) {
                throw new BundleException("No bundle record set mapper for export model: " + model.type());
            }
            recordSets.addAll(mapper.recordSetsFor(model));
        }
        return recordSets;
    }

    /** 写出单个 TSV 文件。 */
    private static void writeRecordSet(BundleRecordSet recordSet, File file) throws BundleException {
        try (TsvRowWriter writer = new TsvRowWriter(file)) {
            writer.writeRow(recordSet.getFields());
            for (List<String> record : recordSet.getRecords()) {
                writer.writeRow(record);
            }
        } catch (IOException e) {
            throw new BundleException("Unable to write TSV file: " + file.getAbsolutePath(), e);
        }
    }
}
