package moe.takochan.webnei.exporter.domain.asset.internal;

/**
 * 由 owner 稳定 ID 推导 asset 在 bundle 内的相对路径。
 *
 * <p>
 * owner ID 是 asset 表与 item/fluid domain 之间的公开契约,文法稳定:
 * <ul>
 * <li>item_variant_id:{@code modId:name@damage} 或 {@code modId:name@damage#nbtHash}
 * <li>fluid_id:{@code modId:name}
 * </ul>
 * 路径统一组织为 {@code <category>/<modId>/<localName>.png}:在首个 {@code :} 处把 modId 拆成目录,
 * 其后的 variant 判别部分作为文件名,忠实保留 ID 文法(damage、nbt 各自可见),仅替换文件系统非法字符。
 */
public final class AssetPath {

    private static final char NAMESPACE_SEPARATOR = ':';
    private static final String CATEGORY_ITEM = "item";
    private static final String CATEGORY_FLUID = "fluid";
    private static final String ITEM_DAMAGE_SEPARATOR = "@";
    private static final String ITEM_NBT_SEPARATOR = "#";
    private static final String PATH_DAMAGE_SEPARATOR = "_m_";
    private static final String PATH_NBT_SEPARATOR = "_n_";
    private static final String PNG_EXTENSION = ".png";
    private static final String ILLEGAL_FILESYSTEM_CHARS = "[<>:\"/\\\\|?*]";

    private AssetPath() {}

    /** item variant 图标路径：{@code item/<modId>/<name_m_damage[_n_nbtHash]>.png}。 */
    public static String itemIcon(String itemVariantId) {
        return path(CATEGORY_ITEM, itemVariantId, true);
    }

    /** fluid 图标路径：{@code fluid/<modId>/<name>.png}。 */
    public static String fluidIcon(String fluidId) {
        return path(CATEGORY_FLUID, fluidId, false);
    }

    private static String path(String category, String ownerId, boolean itemVariant) {
        int namespaceEnd = ownerId.indexOf(NAMESPACE_SEPARATOR);
        String modId = ownerId.substring(0, namespaceEnd);
        String localName = ownerId.substring(namespaceEnd + 1);
        if (itemVariant) {
            localName = localName.replace(ITEM_DAMAGE_SEPARATOR, PATH_DAMAGE_SEPARATOR)
                .replace(ITEM_NBT_SEPARATOR, PATH_NBT_SEPARATOR);
        }
        return category + '/' + safeSegment(modId) + '/' + safeSegment(localName) + PNG_EXTENSION;
    }

    private static String safeSegment(String segment) {
        return segment.replaceAll(ILLEGAL_FILESYSTEM_CHARS, "_");
    }
}
