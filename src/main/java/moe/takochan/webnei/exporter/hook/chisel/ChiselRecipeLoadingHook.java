package moe.takochan.webnei.exporter.hook.chisel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeLoadingHook;
import team.chisel.compat.nei.RecipeHandlerChisel;

/**
 * 触发 Chisel handler 按 {@code outputId="chisel2.chisel"} 全量加载。
 *
 * <p>
 * {@link RecipeHandlerChisel} 的 {@code loadCraftingRecipes(String outputId, ...)} 在 outputId 等于
 * {@code chisel2.chisel}（来自其 transferRect）时遍历 {@code Carving.chisel} 的雕刻组 push 到 {@code arecipes}；
 * 但没重写 {@code getOverlayIdentifier()}，通用 loader 触发不了。输出在 {@code getOtherStacks}。
 */
public final class ChiselRecipeLoadingHook implements IRecipeLoadingHook {

    private static final Map<Class<? extends IRecipeHandler>, String> HANDLER_OUTPUT_IDS = buildHandlerOutputIds();

    private static Map<Class<? extends IRecipeHandler>, String> buildHandlerOutputIds() {
        Map<Class<? extends IRecipeHandler>, String> map = new HashMap<>();
        map.put(RecipeHandlerChisel.class, "chisel2.chisel");
        return map;
    }

    @Override
    public boolean isAvailable() {
        return Mods.CHISEL.isLoaded();
    }

    @Override
    public boolean supports(IRecipeHandler handler) {
        return handler instanceof ICraftingHandler && HANDLER_OUTPUT_IDS.containsKey(handler.getClass());
    }

    @Override
    public List<IRecipeHandler> load(IRecipeHandler handler) {
        String outputId = HANDLER_OUTPUT_IDS.get(handler.getClass());
        if (outputId == null) {
            return Collections.emptyList();
        }
        IRecipeHandler loaded = ((ICraftingHandler) handler).getRecipeHandler(outputId, new Object[0]);
        if (loaded == null || loaded.numRecipes() == 0) {
            return Collections.emptyList();
        }
        return Collections.singletonList(loaded);
    }
}
