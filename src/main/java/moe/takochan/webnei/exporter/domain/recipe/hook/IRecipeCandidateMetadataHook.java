package moe.takochan.webnei.exporter.domain.recipe.hook;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.engine.hook.IExportHook;

/** Collects structured candidate metadata from a handler-owned PositionedStack. */
public interface IRecipeCandidateMetadataHook extends IExportHook {

    boolean supports(IRecipeHandler handler);

    RecipeCandidateMetadata collect(IRecipeHandler handler, int recipeIndex, PositionedStack stack);
}
