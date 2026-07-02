package moe.takochan.webnei.exporter.bundle.pgsql;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

/** 把领域中间模型写成 PostgreSQL 数据导入脚本。 */
public final class PgsqlBundleWriter implements IBundleWriter {

    private static final String OUTPUT_FILE_NAME = "pgsql_data.sql";

    private final BundleRecordSetMapperRegistry mapperRegistry;
    private final AssetBundlePreparer assetBundlePreparer;

    public PgsqlBundleWriter() {
        this(BundleRecordSetMapperRegistry.defaults(), new AssetBundlePreparer());
    }

    public PgsqlBundleWriter(BundleRecordSetMapperRegistry mapperRegistry) {
        this(mapperRegistry, new AssetBundlePreparer());
    }

    public PgsqlBundleWriter(BundleRecordSetMapperRegistry mapperRegistry, AssetBundlePreparer assetBundlePreparer) {
        this.mapperRegistry = mapperRegistry;
        this.assetBundlePreparer = assetBundlePreparer;
    }

    @Override
    public BundleFormat format() {
        return BundleFormat.PGSQL;
    }

    @Override
    public BundleResult write(ExportModelSet models, BundleTarget target, BundleContext context)
        throws BundleException {
        File outputDirectory = new File(target.getOutputDirectory(), BundleOutputPath.forModels(models));
        if (!outputDirectory.isDirectory() && !outputDirectory.mkdirs()) {
            throw new BundleException("Unable to create bundle directory: " + outputDirectory.getAbsolutePath());
        }

        ExportModelSet preparedModels = assetBundlePreparer.prepare(models, outputDirectory, context);
        File file = new File(outputDirectory, OUTPUT_FILE_NAME);
        try {
            new PgsqlScriptWriter().write(recordSets(preparedModels.getModels()), file);
        } catch (IOException e) {
            throw new BundleException("Unable to write PostgreSQL script: " + file.getAbsolutePath(), e);
        }
        return BundleResult.success(format(), Collections.singletonList(file.getAbsolutePath()));
    }

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
}
