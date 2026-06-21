package moe.takochan.webnei.exporter.domain.asset.internal;

public final class AssetPath {

    private static final char NAMESPACE_SEPARATOR = ':';
    private static final String CATEGORY_ITEM = "item";
    private static final String CATEGORY_FLUID = "fluid";
    private static final String CATEGORY_RECIPE = "recipe_category";
    private static final String ITEM_DAMAGE_SEPARATOR = "@";
    private static final String ITEM_NBT_SEPARATOR = "#";
    private static final String PATH_DAMAGE_SEPARATOR = "_m_";
    private static final String PATH_NBT_SEPARATOR = "_n_";
    private static final String ICON_SUFFIX = "_icon";
    private static final String PNG_EXTENSION = ".png";
    private static final String ILLEGAL_FILESYSTEM_CHARS = "[<>:\"/\\\\|?*]";

    private AssetPath() {}

    public static String itemIcon(String itemVariantId) {
        return path(CATEGORY_ITEM, itemVariantId, true, "");
    }

    public static String fluidIcon(String fluidId) {
        return path(CATEGORY_FLUID, fluidId, false, "");
    }

    public static String recipeCategoryIcon(String categoryId) {
        return recipeCategoryPath(categoryId);
    }

    private static String path(String category, String ownerId, boolean itemVariant, String suffix) {
        int namespaceEnd = ownerId.indexOf(NAMESPACE_SEPARATOR);
        String modId = ownerId.substring(0, namespaceEnd);
        String localName = ownerId.substring(namespaceEnd + 1);
        if (itemVariant) {
            localName = localName.replace(ITEM_DAMAGE_SEPARATOR, PATH_DAMAGE_SEPARATOR)
                .replace(ITEM_NBT_SEPARATOR, PATH_NBT_SEPARATOR);
        }
        return category + '/' + safeSegment(modId) + '/' + safeSegment(localName + suffix) + PNG_EXTENSION;
    }

    private static String recipeCategoryPath(String categoryId) {
        return CATEGORY_RECIPE + '/' + safeSegment(categoryId + ICON_SUFFIX) + PNG_EXTENSION;
    }

    private static String safeSegment(String segment) {
        return segment.replaceAll(ILLEGAL_FILESYSTEM_CHARS, "_");
    }
}
