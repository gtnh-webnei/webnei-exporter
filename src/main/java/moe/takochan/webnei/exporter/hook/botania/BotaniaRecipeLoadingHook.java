package moe.takochan.webnei.exporter.hook.botania;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeLoadingHook;
import vazkii.botania.client.integration.nei.recipe.RecipeHandlerBrewery;
import vazkii.botania.client.integration.nei.recipe.RecipeHandlerElvenTrade;
import vazkii.botania.client.integration.nei.recipe.RecipeHandlerManaPool;
import vazkii.botania.client.integration.nei.recipe.RecipeHandlerPetalApothecary;
import vazkii.botania.client.integration.nei.recipe.RecipeHandlerPureDaisy;
import vazkii.botania.client.integration.nei.recipe.RecipeHandlerRunicAltar;

/**
 * 触发 Botania NEI handler 按已知 outputId 加载全量配方。
 *
 * <p>
 * Botania handler 没重写 {@code getOverlayIdentifier()}，默认返回 null，通用 loader 看到 null 就放弃。
 * 但每个 handler 自己的 {@code loadCraftingRecipes(String, Object...)} 在 outputId 命中时会遍历
 * {@link vazkii.botania.api.BotaniaAPI} 的静态配方表 push 到 {@code arecipes}。这里给每个 handler 类静态映射它对应的
 * outputId，再走 {@code getRecipeHandler(outputId, ...)} 让 NEI 走完自家分支。
 */
public final class BotaniaRecipeLoadingHook implements IRecipeLoadingHook {

    /** handler 类 → handler 自己识别的 outputId。 */
    private static final Map<Class<? extends IRecipeHandler>, String> HANDLER_OUTPUT_IDS = buildHandlerOutputIds();

    private static Map<Class<? extends IRecipeHandler>, String> buildHandlerOutputIds() {
        Map<Class<? extends IRecipeHandler>, String> map = new HashMap<>();
        map.put(RecipeHandlerManaPool.class, "botania.manaPool");
        map.put(RecipeHandlerPetalApothecary.class, "botania.petalApothecary");
        map.put(RecipeHandlerPureDaisy.class, "botania.pureDaisy");
        map.put(RecipeHandlerBrewery.class, "botania.brewery");
        map.put(RecipeHandlerRunicAltar.class, "botania.runicAltar");
        map.put(RecipeHandlerElvenTrade.class, "botania.elvenTrade");
        return map;
    }

    @Override
    public boolean isAvailable() {
        return Mods.BOTANIA.isLoaded();
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
