package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeLoadingHook;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeLoadingHookRegistry;

/**
 * 把单个 NEI handler 加载成「已经能枚举配方」的实例集合。
 *
 * <p>
 * 顺序：
 * <ol>
 * <li>先问 {@link IRecipeLoadingHook}，由 mod 专门接管。</li>
 * <li>没有 hook 时，如果该 handler 当前已经有配方页面，直接复用。</li>
 * <li>否则只用 handler 自报的 overlayIdentifier 触发一次合成加载；不猜 key、不试一批 key。</li>
 * </ol>
 * 仍拿不到配方时返回空列表，调用方据此跳过 recipe/slot 导出但保留 category。
 *
 * <p>
 * 需要喂物品才能枚举配方的 handler 不走本类，由 {@link RecipeRegistrar} 在扫描收尾阶段统一对 NEI 全量物品做一次遍历喂入。
 */
public final class RecipeHandlerLoader {

    private final RecipeLoadingHookRegistry hooks;

    public RecipeHandlerLoader(RecipeLoadingHookRegistry hooks) {
        this.hooks = hooks;
    }

    public List<IRecipeHandler> load(IRecipeHandler handler) {
        IRecipeLoadingHook hook = hooks.find(handler);
        if (hook != null) {
            List<IRecipeHandler> loaded = safeHookLoad(hook, handler);
            return filterLoaded(loaded);
        }
        if (safeNumRecipes(handler) > 0) {
            return Collections.singletonList(handler);
        }
        IRecipeHandler triggered = tryTriggerByOverlay(handler);
        if (triggered != null && safeNumRecipes(triggered) > 0) {
            return Collections.singletonList(triggered);
        }
        return Collections.emptyList();
    }

    private static List<IRecipeHandler> safeHookLoad(IRecipeLoadingHook hook, IRecipeHandler handler) {
        try {
            List<IRecipeHandler> loaded = hook.load(handler);
            return loaded == null ? Collections.<IRecipeHandler>emptyList() : loaded;
        } catch (Throwable ignored) {
            return Collections.emptyList();
        }
    }

    private static List<IRecipeHandler> filterLoaded(List<IRecipeHandler> loaded) {
        List<IRecipeHandler> out = new ArrayList<>(loaded.size());
        for (IRecipeHandler handler : loaded) {
            if (handler != null && safeNumRecipes(handler) > 0) {
                out.add(handler);
            }
        }
        return out;
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
