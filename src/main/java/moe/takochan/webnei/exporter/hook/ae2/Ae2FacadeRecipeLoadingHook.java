package moe.takochan.webnei.exporter.hook.ae2;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appeng.integration.modules.NEIHelpers.NEIFacadeRecipeHandler;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeLoadingHook;

/**
 * 触发 AE2 facade handler 按 {@code outputId="crafting"} 加载全量 facade 配方。
 *
 * <p>
 * AE2 facade handler 在 {@code loadCraftingRecipes("crafting", ...)} 且
 * {@code this.getClass() == NEIFacadeRecipeHandler.class} 时遍历 facade list；但没重写
 * {@code getOverlayIdentifier()}。
 */
public final class Ae2FacadeRecipeLoadingHook implements IRecipeLoadingHook {

    private static final Map<Class<? extends IRecipeHandler>, String> HANDLER_OUTPUT_IDS = buildHandlerOutputIds();

    private static Map<Class<? extends IRecipeHandler>, String> buildHandlerOutputIds() {
        Map<Class<? extends IRecipeHandler>, String> map = new HashMap<>();
        map.put(NEIFacadeRecipeHandler.class, "crafting");
        return map;
    }

    @Override
    public boolean isAvailable() {
        return Mods.APPLIED_ENERGISTICS_2.isLoaded();
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
