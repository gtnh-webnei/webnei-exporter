package moe.takochan.webnei.exporter.dataset;

import cpw.mods.fml.common.FMLCommonHandler;
import lombok.Getter;
import moe.takochan.webnei.exporter.export.ExportRequestOptions;
import moe.takochan.webnei.exporter.step.ExportStepContext;

/** 一次导出的 dataset 身份，所有领域模型都通过 dataset_id 关联。 */
@Getter
public final class DatasetIdentity {

    /** 整合包短名称，例如 gtnh。 */
    private final String packSlug;

    /** 整合包版本号。 */
    private final String packVersion;

    /** 数据变体，例如 official、dev 或用户指定变体。 */
    private final String variant;

    /** 当前 Minecraft/Forge 语言代码。 */
    private final String language;

    /** dataset 主键，格式由 pack/version/variant/language 组合决定。 */
    private final String datasetId;

    /** 面向人类展示的 dataset 名称。 */
    private final String displayName;

    private DatasetIdentity(String packSlug, String packVersion, String variant, String language) {
        this.packSlug = packSlug;
        this.packVersion = packVersion;
        this.variant = variant;
        this.language = language;
        this.datasetId = packSlug + ":" + packVersion + ":" + variant + ":" + language;
        this.displayName = packSlug + " " + packVersion + " " + variant + " (" + language + ")";
    }

    public static DatasetIdentity from(ExportStepContext context) {
        return new DatasetIdentity(
            context.executionContext()
                .request()
                .option(ExportRequestOptions.PACK_SLUG),
            context.executionContext()
                .request()
                .option(ExportRequestOptions.PACK_VERSION),
            context.executionContext()
                .request()
                .option(ExportRequestOptions.VARIANT),
            currentLanguage());
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
