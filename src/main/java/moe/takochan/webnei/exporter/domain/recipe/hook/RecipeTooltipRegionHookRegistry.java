package moe.takochan.webnei.exporter.domain.recipe.hook;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.engine.hook.HookRegistry;

/** Collects tooltip regions in HookRegistry order. */
public final class RecipeTooltipRegionHookRegistry {

    private final List<IRecipeTooltipRegionHook> hooks;

    public RecipeTooltipRegionHookRegistry() {
        this.hooks = HookRegistry.get(IRecipeTooltipRegionHook.class);
    }

    public List<RecipeTooltipRegionObservation> collect(IRecipeHandler handler, int recipeIndex) {
        List<RecipeTooltipRegionObservation> out = new ArrayList<>();
        for (IRecipeTooltipRegionHook hook : hooks) {
            if (!hook.supports(handler)) {
                continue;
            }
            out.addAll(
                Objects.requireNonNull(
                    hook.collect(handler, recipeIndex),
                    "Recipe tooltip region hook returned null: " + hook.getClass()
                        .getName()));
        }
        return out;
    }
}
