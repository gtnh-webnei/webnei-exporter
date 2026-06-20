package moe.takochan.webnei.exporter.domain.recipe.hook;

import moe.takochan.webnei.exporter.engine.hook.IExportHook;

public interface IRecipeCategorySkipHook extends IExportHook {

    boolean shouldSkip(RecipeCategoryCandidate category);
}
