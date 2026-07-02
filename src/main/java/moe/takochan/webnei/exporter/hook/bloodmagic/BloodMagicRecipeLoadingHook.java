package moe.takochan.webnei.exporter.hook.bloodmagic;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import WayofTime.alchemicalWizardry.client.nei.NEIAltarRecipeHandler;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeLoadingHook;

/**
 * 触发 Blood Magic 血之祭坛 handler 按 {@code outputId="alchemicalwizardry.altar"} 全量加载。
 *
 * <p>
 * {@link NEIAltarRecipeHandler} 的 {@code getOverlayIdentifier()} 返回 {@code "altarrecipes"}，而
 * {@code loadCraftingRecipes(String outputId, ...)} 只在 outputId 等于 {@code "alchemicalwizardry.altar"}（来自其
 * transferRect）时遍历 {@code AltarRecipeRegistry.altarRecipes} push 到 {@code arecipes}。两者不一致，通用 loader 用
 * overlayId 触发不到全量分支，需显式传 transferRect 的 outputId。
 */
public final class BloodMagicRecipeLoadingHook implements IRecipeLoadingHook {

    private static final Map<Class<? extends IRecipeHandler>, String> HANDLER_OUTPUT_IDS = buildHandlerOutputIds();

    private static Map<Class<? extends IRecipeHandler>, String> buildHandlerOutputIds() {
        Map<Class<? extends IRecipeHandler>, String> map = new HashMap<>();
        map.put(NEIAltarRecipeHandler.class, "alchemicalwizardry.altar");
        return map;
    }

    @Override
    public boolean isAvailable() {
        return Mods.BLOOD_MAGIC.isLoaded();
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
