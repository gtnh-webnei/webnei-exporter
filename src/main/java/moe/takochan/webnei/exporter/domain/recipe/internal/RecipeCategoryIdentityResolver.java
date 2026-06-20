package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.function.Supplier;

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

    private static final String UNKNOWN_MOD_ID = "Unknown";

    public RecipeCategoryIdentity resolve(IRecipeHandler handler) {
        HandlerInfo info = safeHandlerInfo(handler);
        String handlerClass = handler.getClass()
            .getName();
        String handlerId = safeString(handler::getHandlerId);
        String overlayId = safeString(handler::getOverlayIdentifier);
        String modId = modId(info);
        String baseCategoryId = resolveCategoryId(modId, handlerId, overlayId, handlerClass);
        return new RecipeCategoryIdentity(
            handlerKey(handlerClass, handlerId, overlayId),
            baseCategoryId,
            simpleClassName(handlerClass),
            displayName(handler, baseCategoryId),
            modId,
            info == null ? null : info.getItemStack());
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

    private static String resolveCategoryId(String modId, String handlerId, String overlayId, String handlerClass) {
        String namespace = hasText(modId) ? modId : handlerClass;
        String local;
        if (!overlayId.isEmpty() && !isGenericOverlayId(overlayId)) {
            local = overlayId;
        } else if (!handlerId.isEmpty() && !isGenericOverlayId(handlerId)) {
            local = handlerId;
        } else {
            local = handlerClass;
        }
        return sanitize(namespace) + ":" + sanitize(local);
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

    private static String simpleClassName(String className) {
        int index = className.lastIndexOf('.');
        return sanitize(index < 0 ? className : className.substring(index + 1));
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
