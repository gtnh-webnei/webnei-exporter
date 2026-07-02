package moe.takochan.webnei.exporter.engine;

/** 命令请求传给 task 的通用参数 key。 */
public final class ExportRequestOptions {

    /** 整合包 slug。 */
    public static final String PACK_SLUG = "packSlug";

    /** 整合包版本。 */
    public static final String PACK_VERSION = "packVersion";

    /** 数据变体，例如 official。 */
    public static final String VARIANT = "variant";

    /** "true" 表示本次导出整体跳过图片资源渲染（不写图片，也不生成 assets.zip 内的 PNG）。 */
    public static final String SKIP_ASSET_RENDER = "skipAssetRender";

    /** "true" 表示本次导出对动图序列退化为静态首帧（按静态图导出，不生成多帧雪碧图）。 */
    public static final String SKIP_ASSET_ANIMATIONS = "skipAssetAnimations";

    /** 把字符串 option 值解析成布尔，仅 "true"（忽略大小写）视为 true。 */
    public static boolean asBoolean(String value) {
        return value != null && "true".equalsIgnoreCase(value.trim());
    }

    private ExportRequestOptions() {}
}
