package moe.takochan.webnei.exporter.hook.gendustry;

import net.bdew.gendustry.nei.TemplateCraftingHandler;

import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IItemFeedingHook;

/**
 * 声明 gendustry 基因模板 handler 需要喂物品加载。
 *
 * <p>
 * {@link TemplateCraftingHandler} 只重写了 {@code loadCraftingRecipes(ItemStack)}，喂入 {@code GeneTemplate} /
 * {@code GeneSample} 物品时才生成配方，没有 outputId 全量分支。
 */
public final class GendustryTemplateFeedingHook implements IItemFeedingHook {

    @Override
    public boolean isAvailable() {
        return Mods.GENDUSTRY.isLoaded();
    }

    @Override
    public boolean supports(IRecipeHandler handler) {
        return handler instanceof TemplateCraftingHandler;
    }
}
