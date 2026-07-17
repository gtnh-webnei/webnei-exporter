package moe.takochan.webnei.exporter.domain.recipe.hook;

import java.util.List;

import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.engine.hook.IExportHook;

/** Collects non-slot tooltip regions from public recipe-handler data. */
public interface IRecipeTooltipRegionHook extends IExportHook {

    boolean supports(IRecipeHandler handler);

    List<RecipeTooltipRegionObservation> collect(IRecipeHandler handler, int recipeIndex);
}
