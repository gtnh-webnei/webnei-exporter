package moe.takochan.webnei.exporter.hook.ae2;

import appeng.integration.modules.NEIHelpers.NEIWorldCraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IItemFeedingHook;

/**
 * 声明 AE2 世界内合成 handler 需要喂物品加载。
 *
 * <p>
 * {@link NEIWorldCraftingHandler} 没有 outputId 全量分支，{@code getRecipeHandler("item", stack)} 在 stack 命中
 * 充能石英 / 压印板 / fluix 等世界内合成产物时生成一条无输入、单输出的配方。
 */
public final class Ae2WorldCraftingFeedingHook implements IItemFeedingHook {

    @Override
    public boolean isAvailable() {
        return Mods.APPLIED_ENERGISTICS_2.isLoaded();
    }

    @Override
    public boolean supports(IRecipeHandler handler) {
        return handler instanceof NEIWorldCraftingHandler;
    }
}
