package moe.takochan.webnei.exporter.hook.neiaddons;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bdew.neiaddons.botany.flowers.FlowerBreedingHandler;
import net.bdew.neiaddons.forestry.bees.BeeBreedingHandler;
import net.bdew.neiaddons.forestry.bees.BeeProduceHandler;
import net.bdew.neiaddons.forestry.trees.TreeBreedingHandler;
import net.bdew.neiaddons.forestry.trees.TreeProduceHandler;

import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeLoadingHook;

/**
 * 触发 NEIAddons 的 Forestry / Botany 育种 handler 按 {@code getRecipeIdent()} 全量加载。
 *
 * <p>
 * bdew 的 {@code BaseBreedingRecipeHandler.loadCraftingRecipes(outputId, ...)} 命中 {@code getRecipeIdent()}
 * 时遍历对应 species root 的 mutations；但没重写 {@code getOverlayIdentifier()}。
 */
public final class NeiAddonsRecipeLoadingHook implements IRecipeLoadingHook {

    private static final Map<Class<? extends IRecipeHandler>, String> HANDLER_OUTPUT_IDS = buildHandlerOutputIds();

    private static Map<Class<? extends IRecipeHandler>, String> buildHandlerOutputIds() {
        Map<Class<? extends IRecipeHandler>, String> map = new HashMap<>();
        map.put(BeeBreedingHandler.class, "beebreeding");
        map.put(BeeProduceHandler.class, "beeproduce");
        map.put(TreeBreedingHandler.class, "treebreeding");
        map.put(TreeProduceHandler.class, "treeproduce");
        map.put(FlowerBreedingHandler.class, "flowerbreeding");
        return map;
    }

    @Override
    public boolean isAvailable() {
        return Mods.NEI_ADDONS.isLoaded();
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
