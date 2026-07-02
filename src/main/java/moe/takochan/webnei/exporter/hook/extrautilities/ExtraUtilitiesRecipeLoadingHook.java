package moe.takochan.webnei.exporter.hook.extrautilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rwtema.extrautils.nei.FMPMicroBlocksHandler;

import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeLoadingHook;

/**
 * 触发 Extra Utilities FMP 微型方块 handler 按 {@code outputId="microblocks"} 全量加载。
 *
 * <p>
 * {@link FMPMicroBlocksHandler} 的 {@code loadCraftingRecipes(String outputId, ...)} 在 outputId 等于其
 * {@code identifier}（{@code "microblocks"}）时遍历 {@code getCraftingRecipes()} push 到 {@code arecipes}；
 * 但没重写 {@code getOverlayIdentifier()}，通用 loader 触发不了。
 */
public final class ExtraUtilitiesRecipeLoadingHook implements IRecipeLoadingHook {

    private static final Map<Class<? extends IRecipeHandler>, String> HANDLER_OUTPUT_IDS = buildHandlerOutputIds();

    private static Map<Class<? extends IRecipeHandler>, String> buildHandlerOutputIds() {
        Map<Class<? extends IRecipeHandler>, String> map = new HashMap<>();
        map.put(FMPMicroBlocksHandler.class, "microblocks");
        return map;
    }

    @Override
    public boolean isAvailable() {
        return Mods.EXTRA_UTILITIES.isLoaded();
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
