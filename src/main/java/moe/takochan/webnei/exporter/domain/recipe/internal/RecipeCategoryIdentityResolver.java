package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;

import codechicken.nei.drawable.DrawableResource;
import codechicken.nei.recipe.GuiRecipeTab;
import codechicken.nei.recipe.HandlerInfo;
import codechicken.nei.recipe.IRecipeHandler;

/**
 * 把单个 NEI {@link IRecipeHandler} 解析为 {@link RecipeCategoryIdentity}。
 *
 * <p>
 * mod_id 遵循 mod domain 的空值规则：没有可靠值时为空字符串，不使用 unknown 兜底。
 */
public final class RecipeCategoryIdentityResolver {

    /** NEI 对未知归属返回的占位 mod id；遇到时归一化为空字符串，与 mod domain 的空值规则保持一致。 */
    private static final String UNKNOWN_MOD_ID = "Unknown";

    public RecipeCategoryIdentity resolve(IRecipeHandler handler) {
        HandlerInfo info = safeHandlerInfo(handler);
        String handlerClass = handler.getClass()
            .getName();
        String handlerId = safeString(handler::getHandlerId);
        String overlayId = safeString(handler::getOverlayIdentifier);
        String categoryId = resolveCategoryId(handlerClass, handlerId, overlayId);
        return new RecipeCategoryIdentity(
            handlerKey(handlerClass, handlerId, overlayId),
            categoryId,
            displayName(handler, categoryId),
            modId(info),
            iconStack(info),
            iconImage(info));
    }

    private static ItemStack iconStack(HandlerInfo info) {
        return info == null ? null : info.getItemStack();
    }

    private static DrawableResource iconImage(HandlerInfo info) {
        return info == null ? null : info.getImage();
    }

    private static String handlerKey(String handlerClass, String handlerId, String overlayId) {
        return handlerClass + "::" + handlerId + "::" + overlayId;
    }

    private static String displayName(IRecipeHandler handler, String categoryId) {
        String recipeName = safeString(handler::getRecipeName).trim();
        if (hasText(recipeName)) {
            return recipeName;
        }
        String recipeTabName = safeString(handler::getRecipeTabName).trim();
        if (hasText(recipeTabName)) {
            return recipeTabName;
        }
        return categoryId;
    }

    private static String modId(HandlerInfo info) {
        if (info == null) {
            return "";
        }
        String modId = nullToEmpty(info.getModId()).trim();
        return UNKNOWN_MOD_ID.equals(modId) ? "" : modId;
    }

    /**
     * 由 handler 身份确定性地派生分类 ID。
     *
     * <p>
     * 始终以 handlerClass 为基础段，保证不同 mod / 不同实现互不撞名；再按需追加 handlerId、overlayId 中能提供额外区分度的部分（与 class
     * 不同且非通用值），用来区分同一个 handlerClass 注册的多个分类（例如 NEI Custom Diagram 的多张图、IC2 Metal Former 的多种模式）。
     *
     * <p>
     * 该 ID 不读取运行时 modId，也不依赖本次扫描里是否存在撞名 handler，因此对同一个 handler 恒定。
     */
    private static String resolveCategoryId(String handlerClass, String handlerId, String overlayId) {
        StringBuilder categoryId = new StringBuilder(sanitize(handlerClass));
        appendIfDistinct(categoryId, sanitize(handlerId));
        appendIfDistinct(categoryId, sanitize(overlayId));
        return categoryId.toString();
    }

    /** 当片段非空、非通用值，且未出现在已有 ID 中时，追加它以区分同类的多个分类。 */
    private static void appendIfDistinct(StringBuilder categoryId, String segment) {
        if (segment.isEmpty() || isGenericOverlayId(segment)) {
            return;
        }
        if (categoryId.indexOf(segment) >= 0) {
            return;
        }
        categoryId.append('.')
            .append(segment);
    }

    private static boolean isGenericOverlayId(String id) {
        return "crafting".equals(id) || "item".equals(id);
    }

    private static HandlerInfo safeHandlerInfo(IRecipeHandler handler) {
        try {
            return GuiRecipeTab.getHandlerInfo(handler);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static String safeString(Supplier<String> getter) {
        try {
            return nullToEmpty(getter.get());
        } catch (Throwable ignored) {
            return "";
        }
    }

    /**
     * 把 NEI 字符串规范化为 category_id 的一段（namespace 或 local）。
     *
     * <p>
     * NEI handler id（overlayId/handlerId，以及 fallback 的类全名）没有字符集约束，实测普遍含大写。category_id 会作为 DB key、asset 路径和 URL
     * key 使用，所以这里统一转小写，得到稳定的 ID 片段。
     *
     * <p>
     * 由于契约不限制字符集，非 {@code a-z 0-9 . _ -} 的字符转成 {@code _}，段内 {@code :} 转成 {@code .} 作为防御性兜底。
     */
    private static String sanitize(String value) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = Character.toLowerCase(value.charAt(i));
            if (c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.' || c == '_' || c == '-') {
                out.append(c);
            } else if (c == ':') {
                out.append('.');
            } else {
                out.append('_');
            }
        }
        return out.toString();
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim()
            .isEmpty();
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
