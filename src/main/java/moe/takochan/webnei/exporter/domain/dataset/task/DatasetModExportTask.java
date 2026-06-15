package moe.takochan.webnei.exporter.domain.dataset.task;

import cpw.mods.fml.common.FMLCommonHandler;
import moe.takochan.webnei.exporter.domain.dataset.store.DatasetDomainStore;
import moe.takochan.webnei.exporter.engine.ExportRequestOptions;
import moe.takochan.webnei.exporter.engine.task.ExportTaskContext;
import moe.takochan.webnei.exporter.engine.task.IExportTask;

/**
 * dataset 数据域导出任务。
 *
 * <p>
 * 从请求参数构建 dataset 信息并注册 DatasetDomainStore，供后续所有 domain 获取 dataset_id。
 */
public final class DatasetModExportTask implements IExportTask {

    public static final String ID = "dataset-export";

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
        String packSlug = context.executionContext().request().option(ExportRequestOptions.PACK_SLUG);
        String packVersion = context.executionContext().request().option(ExportRequestOptions.PACK_VERSION);
        String variant = context.executionContext().request().option(ExportRequestOptions.VARIANT);
        String language = currentLanguage();

        DatasetDomainStore store = new DatasetDomainStore();
        store.initialize(new DatasetDomainStore.Input(packSlug, packVersion, variant, language));
        context.register(DatasetDomainStore.class, store);
    }

    private static String currentLanguage() {
        try {
            String language = FMLCommonHandler.instance().getCurrentLanguage();
            if (language != null && !language.trim().isEmpty()) {
                return language.trim();
            }
        } catch (Throwable ignored) {}
        return "en_US";
    }
}
