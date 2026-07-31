package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.Collections;
import java.util.List;

import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;

/**
 * 把单个 NEI handler 加载成已经能枚举配方的实例集合。
 *
 * <p>
 * handler 当前已有配方页面时直接复用；否则只用 handler 自报的 overlayIdentifier 触发一次合成加载，不猜 key、
 * 不批量尝试额外 key。仍拿不到配方时返回空列表，调用方保留 category，但不导出 recipe/slot。
 */
public final class RecipeHandlerLoader {

    public List<IRecipeHandler> load(IRecipeHandler handler) {
        if (safeNumRecipes(handler) > 0) {
            return Collections.singletonList(handler);
        }
        IRecipeHandler triggered = tryTriggerByOverlay(handler);
        if (triggered != null && safeNumRecipes(triggered) > 0) {
            return Collections.singletonList(triggered);
        }
        return Collections.emptyList();
    }

    /** 仅当 handler 是 crafting handler 并且有非空 overlayIdentifier 时，按 handler 自己的 overlay 触发加载。 */
    private static IRecipeHandler tryTriggerByOverlay(IRecipeHandler handler) {
        if (!(handler instanceof ICraftingHandler)) {
            return null;
        }
        String overlay;
        try {
            overlay = handler.getOverlayIdentifier();
        } catch (Throwable ignored) {
            return null;
        }
        if (overlay == null || overlay.isEmpty()) {
            return null;
        }
        try {
            return ((ICraftingHandler) handler).getRecipeHandler(overlay, new Object[0]);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static int safeNumRecipes(IRecipeHandler handler) {
        try {
            return handler.numRecipes();
        } catch (Throwable ignored) {
            return 0;
        }
    }
}
