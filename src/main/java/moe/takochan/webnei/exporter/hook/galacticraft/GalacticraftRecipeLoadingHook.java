package moe.takochan.webnei.exporter.hook.galacticraft;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import micdoodle8.mods.galacticraft.core.nei.BuggyRecipeHandler;
import micdoodle8.mods.galacticraft.planets.asteroids.nei.AstroMinerRecipeHandler;
import micdoodle8.mods.galacticraft.planets.mars.nei.CargoRocketRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeLoadingHook;

/**
 * 触发 Galacticraft NASA 工作台系列 handler 按各自 {@code getRecipeId()} 全量加载。
 *
 * <p>
 * buggy / astroMiner / cargoRocket 三个 handler 都继承 {@code TemplateRecipeHandler}，
 * {@code loadCraftingRecipes(String outputId, ...)} 在 outputId 命中时遍历自家静态配方表 push 到 {@code arecipes}；
 * 但都没重写 {@code getOverlayIdentifier()}，通用 loader 拿不到 outputId 触发不了。这里按类映射 outputId，
 * 走 {@code getRecipeHandler(outputId, ...)} 让 handler 走自家全量分支。
 */
public final class GalacticraftRecipeLoadingHook implements IRecipeLoadingHook {

    private static final Map<Class<? extends IRecipeHandler>, String> HANDLER_OUTPUT_IDS = buildHandlerOutputIds();

    private static Map<Class<? extends IRecipeHandler>, String> buildHandlerOutputIds() {
        Map<Class<? extends IRecipeHandler>, String> map = new HashMap<>();
        map.put(BuggyRecipeHandler.class, "galacticraft.buggy");
        map.put(AstroMinerRecipeHandler.class, "galacticraft.astroMiner");
        map.put(CargoRocketRecipeHandler.class, "galacticraft.cargoRocket");
        return map;
    }

    @Override
    public boolean isAvailable() {
        return Mods.GALACTICRAFT_CORE.isLoaded();
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
