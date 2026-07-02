package moe.takochan.webnei.exporter.hook.amunra;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import de.katzenpapst.amunra.nei.recipehandler.ARNasaWorkbenchShuttle;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeLoadingHook;

/**
 * 触发 AmunRa NASA 工作台（航天飞机）handler 按 {@code getRecipeId()} 全量加载。
 *
 * <p>
 * {@link ARNasaWorkbenchShuttle} 继承 {@code TemplateRecipeHandler}，{@code loadCraftingRecipes(String outputId, ...)}
 * 在 outputId 等于 {@code amunra.rocketShuttle} 时遍历 {@code NEIAmunRaConfig.getShuttleRecipes()} push 到
 * {@code arecipes}；但没重写 {@code getOverlayIdentifier()}，通用 loader 触发不了。
 */
public final class AmunRaRecipeLoadingHook implements IRecipeLoadingHook {

    private static final Map<Class<? extends IRecipeHandler>, String> HANDLER_OUTPUT_IDS = buildHandlerOutputIds();

    private static Map<Class<? extends IRecipeHandler>, String> buildHandlerOutputIds() {
        Map<Class<? extends IRecipeHandler>, String> map = new HashMap<>();
        map.put(ARNasaWorkbenchShuttle.class, "amunra.rocketShuttle");
        return map;
    }

    @Override
    public boolean isAvailable() {
        return Mods.AMUNRA.isLoaded();
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
