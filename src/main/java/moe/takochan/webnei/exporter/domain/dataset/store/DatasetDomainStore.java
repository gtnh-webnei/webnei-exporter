package moe.takochan.webnei.exporter.domain.dataset.store;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cpw.mods.fml.common.Loader;
import moe.takochan.webnei.exporter.Tags;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.dataset.DatasetExportModel;
import moe.takochan.webnei.exporter.domain.dataset.model.DatasetRow;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/**
 * dataset domain store。
 *
 * <p>
 * 持有本次导出的 DatasetRow，供其他 domain 通过 store 获取 dataset_id 等信息。
 * 外部只需 {@code context.store(DatasetDomainStore.class).get(null).getDatasetId()}。
 */
public final class DatasetDomainStore implements IDomainStore {

    private DatasetRow row;

        public DatasetRow add(Input input) {
        this.row = new DatasetRow(
            buildDatasetId(input),
            input.packSlug,
            input.packVersion,
            input.variant,
            input.language,
            buildDisplayName(input),
            SCHEMA_VERSION,
            Tags.VERSION,
            exportedAt(),
            Loader.MC_VERSION);
        return row;
    }

        public DatasetRow get(String key) {
        return row;
    }

        public List<DatasetRow> list() {
        return row == null ? Collections.emptyList() : Collections.singletonList(row);
    }

    @Override
    public IExportModel toExportModel() {
        return row == null ? null : new DatasetExportModel(row);
    }

    private static String buildDatasetId(Input input) {
        return input.packSlug + ":" + input.packVersion + ":" + input.variant + ":" + input.language;
    }

    private static String buildDisplayName(Input input) {
        return input.packSlug + " " + input.packVersion + " " + input.variant + " (" + input.language + ")";
    }

    private static String exportedAt() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(new Date());
    }

    private static final String SCHEMA_VERSION = "3";

    /** dataset store 的输入参数。 */
    public static final class Input {

        private final String packSlug;
        private final String packVersion;
        private final String variant;
        private final String language;

        public Input(String packSlug, String packVersion, String variant, String language) {
            this.packSlug = packSlug;
            this.packVersion = packVersion;
            this.variant = variant;
            this.language = language;
        }
    }
}
