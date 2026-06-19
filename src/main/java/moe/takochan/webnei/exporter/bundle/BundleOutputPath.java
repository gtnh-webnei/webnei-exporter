package moe.takochan.webnei.exporter.bundle;

import java.io.File;

import moe.takochan.webnei.exporter.domain.ExportModelSet;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.dataset.DatasetExportModel;
import moe.takochan.webnei.exporter.domain.dataset.model.DatasetRow;

/** bundle 输出目录路径规则。 */
public final class BundleOutputPath {

    private BundleOutputPath() {}

    /** 根据 dataset 信息生成 bundle 输出目录。 */
    public static String forModels(ExportModelSet models) {
        for (IExportModel model : models.getModels()) {
            if (model instanceof DatasetExportModel datasetMod) {
                return datasetOutputPath(datasetMod.getDataset());
            }
        }
        return sanitizePathSegment(models.getPlanId());
    }

    private static String datasetOutputPath(DatasetRow dataset) {
        return sanitizePathSegment(dataset.getPackSlug()) + File.separator
            + sanitizePathSegment(dataset.getPackVersion())
            + File.separator
            + sanitizePathSegment(dataset.getVariant())
            + File.separator
            + sanitizePathSegment(dataset.getLanguage());
    }

    private static String sanitizePathSegment(String value) {
        String sanitized = value.trim()
            .replaceAll("[^A-Za-z0-9._-]", "_");
        sanitized = sanitized.replaceAll("^\\.+", "")
            .replaceAll("\\.+$", "");
        return sanitized.isEmpty() ? "value" : sanitized;
    }
}
