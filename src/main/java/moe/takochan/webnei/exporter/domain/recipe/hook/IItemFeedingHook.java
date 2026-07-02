package moe.takochan.webnei.exporter.domain.recipe.hook;

import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.engine.hook.IExportHook;

/**
 * 喂物品加载钩子：声明某个 NEI handler 必须通过「逐个物品喂入」才能枚举出全量配方。
 *
 * <p>
 * 有些 handler 没有 {@code loadCraftingRecipes(String outputId)} 全量分支，只在玩家对着具体物品按查合成 /
 * 查用途时，由 {@code getRecipeHandler("item", stack)} / {@code getUsageHandler("item", stack)} 现场判断该物品能否
 * 命中自己的配方表。这类 handler 无法靠 outputId 触发，只能把 NEI 全量物品逐个喂进去。
 *
 * <p>
 * 本钩子只回答「这个 handler 是否需要喂物品」；具体喂哪些物品（NEI {@code ItemList.items} 全量）与喂入执行，
 * 由通用 loader 统一负责，hook 不持有物品来源，避免落入按 mod 复刻配方表的老路。
 */
public interface IItemFeedingHook extends IExportHook {

    /** 该 handler 是否需要喂物品才能枚举配方。 */
    boolean supports(IRecipeHandler handler);
}
