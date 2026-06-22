package moe.takochan.webnei.exporter.domain.dataset.internal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import cpw.mods.fml.common.Loader;
import moe.takochan.webnei.exporter.Tags;
import moe.takochan.webnei.exporter.domain.dataset.model.DatasetRow;
import moe.takochan.webnei.exporter.engine.store.IDomainRegistrar;

public final class DatasetRegistrar implements IDomainRegistrar {

    private static final String SCHEMA_VERSION = "3";

    private final DatasetDomainData data;

    public DatasetRegistrar(DatasetDomainData data) {
        this.data = data;
    }

    public void register(String packSlug, String packVersion, String variant, String language) {
        data.setRow(
            new DatasetRow(
                buildDatasetId(packSlug, packVersion, variant, language),
                packSlug,
                packVersion,
                variant,
                language,
                buildDisplayName(packSlug, packVersion, variant, language),
                SCHEMA_VERSION,
                Tags.VERSION,
                exportedAt(),
                Loader.MC_VERSION));
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
