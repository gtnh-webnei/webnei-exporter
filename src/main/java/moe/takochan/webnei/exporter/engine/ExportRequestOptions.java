package moe.takochan.webnei.exporter.engine;

/** 命令请求传给 task 的通用参数 key。 */
public final class ExportRequestOptions {

    /** 整合包 slug。 */
    public static final String PACK_SLUG = "packSlug";

    /** 整合包版本。 */
    public static final String PACK_VERSION = "packVersion";

    /** 数据变体，例如 official。 */
    public static final String VARIANT = "variant";

    private ExportRequestOptions() {}
}
