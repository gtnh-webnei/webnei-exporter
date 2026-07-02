package moe.takochan.webnei.exporter.hook.gtnhintergalactic;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import gtnhintergalactic.nei.GasSiphonRecipeHandler;
import gtnhintergalactic.nei.SpacePumpModuleRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeLoadingHook;

/**
 * 触发 GTNH Intergalactic 的 NEI handler 按 {@code getOutputId()} 全量加载。
 *
 * <p>
 * gtnhintergalactic 的 handler 在 {@code loadCraftingRecipes(outputId, ...)} 命中 {@code getOutputId()}
 * 时遍历静态 {@code RECIPES} 表；但没重写 {@code getOverlayIdentifier()}。
 */
public final class GtnhIntergalacticRecipeLoadingHook implements IRecipeLoadingHook {

    private static final Map<Class<? extends IRecipeHandler>, String> HANDLER_OUTPUT_IDS = buildHandlerOutputIds();

    private static Map<Class<? extends IRecipeHandler>, String> buildHandlerOutputIds() {
        Map<Class<? extends IRecipeHandler>, String> map = new HashMap<>();
        map.put(GasSiphonRecipeHandler.class, "galacticraft.siphon");
        map.put(SpacePumpModuleRecipeHandler.class, "galacticraft.elevatorPump");
        return map;
    }

    @Override
    public boolean isAvailable() {
        return Mods.GTNH_INTERGALACTIC.isLoaded();
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
