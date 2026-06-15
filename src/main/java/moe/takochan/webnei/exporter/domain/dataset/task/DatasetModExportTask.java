package moe.takochan.webnei.exporter.domain.dataset.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import cpw.mods.fml.common.Loader;
import moe.takochan.webnei.exporter.Tags;
import moe.takochan.webnei.exporter.engine.task.ExportTaskContext;
import moe.takochan.webnei.exporter.engine.task.IExportTask;
import moe.takochan.webnei.exporter.domain.dataset.DatasetExportModel;
import moe.takochan.webnei.exporter.domain.dataset.model.DatasetRow;

/**
 * dataset 基础数据导出任务。
 *
 * <p>
 * dataset 是一次导出的核心关联索引。后续 item、fluid、recipe、asset 等数据都必须挂在同一个
 */
public final class DatasetModExportTask implements IExportTask {

    public static final String ID = "dataset-export";
    public static final String SCHEMA_VERSION = "2";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String labelKey() {
        return "webnei.task.dataset";
    }

    @Override
    public void execute(ExportTaskContext context) {
        DatasetIdentity identity = DatasetIdentity.from(context);
        context.addModel(new DatasetExportModel(datasetRow(identity)));
    }

    private static DatasetRow datasetRow(DatasetIdentity identity) {
        return new DatasetRow(
            identity.getDatasetId(),
            identity.getPackSlug(),
            identity.getPackVersion(),
            identity.getVariant(),
            identity.getLanguage(),
            identity.getDisplayName(),
            SCHEMA_VERSION,
            Tags.VERSION,
            exportedAt(),
            Loader.MC_VERSION);
    }

    private static String exportedAt() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(new Date());
    }
}
