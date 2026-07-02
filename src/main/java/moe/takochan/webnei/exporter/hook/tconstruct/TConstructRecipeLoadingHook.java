package moe.takochan.webnei.exporter.hook.tconstruct;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeLoadingHook;
import tconstruct.plugins.nei.RecipeHandlerAlloying;
import tconstruct.plugins.nei.RecipeHandlerCastingBasin;
import tconstruct.plugins.nei.RecipeHandlerCastingTable;
import tconstruct.plugins.nei.RecipeHandlerDryingRack;
import tconstruct.plugins.nei.RecipeHandlerMelting;

/**
 * 触发 Tinkers' Construct NEI handler 按 handler 自己的 {@code getRecipeID()} 全量加载。
 *
 * <p>
 * TConstruct handler 都没重写 {@code getOverlayIdentifier()}，通用 loader 拿不到 outputId 触发不了
 * {@code loadCraftingRecipes(String, Object...)} 的全量分支。这里直接按类映射 outputId，调
 * {@code getRecipeHandler(outputId, ...)} 让 handler 走自家全量分支。
 *
 * <p>
 * 注：CastingBasin / CastingTable 共享 outputId {@code tconstruct.smeltery.casting}，但因为
 * {@code newInstance()} 反射用 {@code getClass()} 重建当前类实例，每个 handler 仍能拿到自家的
 * {@code getCastingRecipes()} 子集（basin 配方 vs table 配方）。
 */
public final class TConstructRecipeLoadingHook implements IRecipeLoadingHook {

    private static final Map<Class<? extends IRecipeHandler>, String> HANDLER_OUTPUT_IDS = buildHandlerOutputIds();

    private static Map<Class<? extends IRecipeHandler>, String> buildHandlerOutputIds() {
        Map<Class<? extends IRecipeHandler>, String> map = new HashMap<>();
        map.put(RecipeHandlerAlloying.class, "tconstruct.smeltery.alloying");
        map.put(RecipeHandlerCastingBasin.class, "tconstruct.smeltery.casting");
        map.put(RecipeHandlerCastingTable.class, "tconstruct.smeltery.casting");
        map.put(RecipeHandlerDryingRack.class, "tconstruct.dryingrack");
        map.put(RecipeHandlerMelting.class, "tconstruct.smeltery.melting");
        return map;
    }

    @Override
    public boolean isAvailable() {
        return Mods.TCONSTRUCT.isLoaded();
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
