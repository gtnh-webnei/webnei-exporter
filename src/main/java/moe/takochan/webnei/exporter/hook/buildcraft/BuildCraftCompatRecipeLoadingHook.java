package moe.takochan.webnei.exporter.hook.buildcraft;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import buildcraft.compat.nei.RecipeHandlerAssemblyTable;
import buildcraft.compat.nei.RecipeHandlerIntegrationTable;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeLoadingHook;

/**
 * 触发 BuildCraft|Compat 的 NEI handler 按 {@code getRecipeID()} 全量加载。
 *
 * <p>
 * BC|Compat 的 {@code RecipeHandlerBase.loadCraftingRecipes(outputId, ...)} 命中 {@code getRecipeID()}
 * 时调 {@code loadAllRecipes()}；但没重写 {@code getOverlayIdentifier()}，通用 loader 触发不了。
 */
public final class BuildCraftCompatRecipeLoadingHook implements IRecipeLoadingHook {

    private static final Map<Class<? extends IRecipeHandler>, String> HANDLER_OUTPUT_IDS = buildHandlerOutputIds();

    private static Map<Class<? extends IRecipeHandler>, String> buildHandlerOutputIds() {
        Map<Class<? extends IRecipeHandler>, String> map = new HashMap<>();
        map.put(RecipeHandlerAssemblyTable.class, "buildcraft.assemblyTable");
        map.put(RecipeHandlerIntegrationTable.class, "buildcraft.integrationTable");
        return map;
    }

    @Override
    public boolean isAvailable() {
        return Mods.BUILDCRAFT_COMPAT.isLoaded();
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
