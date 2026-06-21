package moe.takochan.webnei.exporter.domain.dataset.internal;

import cpw.mods.fml.common.FMLCommonHandler;
import moe.takochan.webnei.exporter.engine.ExportRequestOptions;
import moe.takochan.webnei.exporter.engine.task.ExportTaskContext;

public final class DatasetRequestSource {

    private final DatasetRegistrar registrar;
    private final ExportTaskContext context;

    public DatasetRequestSource(DatasetRegistrar registrar, ExportTaskContext context) {
        this.registrar = registrar;
        this.context = context;
    }

    public void collect() {
        String packSlug = context.executionContext()
            .request()
            .option(ExportRequestOptions.PACK_SLUG);
        String packVersion = context.executionContext()
            .request()
            .option(ExportRequestOptions.PACK_VERSION);
        String variant = context.executionContext()
            .request()
            .option(ExportRequestOptions.VARIANT);
        this.registrar.register(packSlug, packVersion, variant, currentLanguage());
    }

    private static String currentLanguage() {
        try {
            String language = FMLCommonHandler.instance()
                .getCurrentLanguage();
            if (language != null && !language.trim()
                .isEmpty()) {
                return language.trim();
            }
        } catch (Throwable ignored) {}
        return "en_US";
    }
}
