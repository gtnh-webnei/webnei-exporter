package moe.takochan.webnei.exporter.domain.dataset.internal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import cpw.mods.fml.common.Loader;
import moe.takochan.webnei.exporter.Tags;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.dataset.DatasetExportModel;
import moe.takochan.webnei.exporter.domain.dataset.model.DatasetRow;

/**
 * dataset domain store 的内部数据和构造逻辑。
 *
 * <p>
 * 该类可以直接持有 row model 和构造细节；跨 domain 调用方只能通过 DatasetDomainStore 访问需要的信息。
 */
public final class DatasetDomainData {

    private static final String SCHEMA_VERSION = "3";

    private DatasetRow row;

    public void initialize(String packSlug, String packVersion, String variant, String language) {
        row = new DatasetRow(
            buildDatasetId(packSlug, packVersion, variant, language),
            packSlug,
            packVersion,
            variant,
            language,
            buildDisplayName(packSlug, packVersion, variant, language),
            SCHEMA_VERSION,
            Tags.VERSION,
            exportedAt(),
            Loader.MC_VERSION);
    }

    public String datasetId() {
        return requireRow().getDatasetId();
    }

    public IExportModel toExportModel() {
        return row == null ? null : new DatasetExportModel(row);
    }

    private DatasetRow requireRow() {
        if (row == null) {
            throw new IllegalStateException("Dataset domain store is not initialized");
        }
        return row;
    }

    private static String buildDatasetId(String packSlug, String packVersion, String variant, String language) {
        return packSlug + ":" + packVersion + ":" + variant + ":" + language;
    }

    private static String buildDisplayName(String packSlug, String packVersion, String variant, String language) {
        return packSlug + " " + packVersion + " " + variant + " (" + language + ")";
    }

    private static String exportedAt() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(new Date());
    }
}
