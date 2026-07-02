package moe.takochan.webnei.exporter.hook.projectblue;

import codechicken.nei.recipe.IRecipeHandler;
import gcewing.projectblue.nei.NEIRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IItemFeedingHook;

/**
 * 声明 Project Blue 控制面板 handler 需要喂物品加载。
 *
 * <p>
 * {@link NEIRecipeHandler} 只重写了 {@code loadCraftingRecipes(ItemStack)} / {@code loadUsageRecipes(ItemStack)}，
 * 喂入物品时按 {@code ControlPanelRecipes.recipes} 匹配生成配方，没有 outputId 全量分支。
 */
public final class ProjectBlueFeedingHook implements IItemFeedingHook {

    @Override
    public boolean isAvailable() {
        return Mods.PROJECT_BLUE.isLoaded();
    }

    @Override
    public boolean supports(IRecipeHandler handler) {
        return handler instanceof NEIRecipeHandler;
    }
}
