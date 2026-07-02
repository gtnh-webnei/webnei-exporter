package moe.takochan.webnei.exporter.hook.neiintegration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeLoadingHook;
import tonius.neiintegration.RecipeHandlerBase;
import tonius.neiintegration.mods.railcraft.RecipeHandlerBlastFurnace;
import tonius.neiintegration.mods.railcraft.RecipeHandlerCokeOven;
import tonius.neiintegration.mods.railcraft.RecipeHandlerRollingMachineShaped;

/**
 * 触发 NEI-Integration Railcraft handler 按 {@code getRecipeID()} 全量加载。
 *
 * <p>
 * NEI-Integration 的 {@link RecipeHandlerBase} 在 {@code loadCraftingRecipes(outputId, ...)} 命中
 * {@code getRecipeID()} 时调 {@code loadAllRecipes()}；但没重写 {@code getOverlayIdentifier()}，通用 loader 拿不到 outputId
 * 触发不了。本 hook 只接 railcraft 子包的 handler，避免影响该 mod 在其他游戏内已正常工作的 handler。
 */
public final class NeiIntegrationRailcraftRecipeLoadingHook implements IRecipeLoadingHook {

    private static final Map<Class<? extends IRecipeHandler>, String> HANDLER_OUTPUT_IDS = buildHandlerOutputIds();

    private static Map<Class<? extends IRecipeHandler>, String> buildHandlerOutputIds() {
        Map<Class<? extends IRecipeHandler>, String> map = new HashMap<>();
        map.put(RecipeHandlerBlastFurnace.class, "railcraft.blastfurnace");
        map.put(RecipeHandlerCokeOven.class, "railcraft.cokeoven");
        map.put(RecipeHandlerRollingMachineShaped.class, "railcraft.rollingmachine");
        return map;
    }

    @Override
    public boolean isAvailable() {
        return Mods.NEI_INTEGRATION.isLoaded();
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
