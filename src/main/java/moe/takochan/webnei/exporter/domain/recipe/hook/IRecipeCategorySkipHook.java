package moe.takochan.webnei.exporter.domain.recipe.hook;

import moe.takochan.webnei.exporter.engine.hook.IExportHook;

/** 配方分类跳过钩子：按 category_id 决定某个分类是否不参与导出。 */
public interface IRecipeCategorySkipHook extends IExportHook {

    boolean shouldSkip(String categoryId);
}
