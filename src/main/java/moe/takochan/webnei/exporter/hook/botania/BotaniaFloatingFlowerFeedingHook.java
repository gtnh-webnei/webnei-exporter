package moe.takochan.webnei.exporter.hook.botania;

import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IItemFeedingHook;
import vazkii.botania.client.integration.nei.recipe.RecipeHandlerFloatingFlowers;

/**
 * 声明 Botania 浮空花 handler 需要喂物品加载。
 *
 * <p>
 * {@link RecipeHandlerFloatingFlowers} 没有 {@code loadCraftingRecipes(String)} 全量分支，只在喂入
 * {@code BlockFloatingSpecialFlower} / {@code BlockSpecialFlower} 物品时逐花生成配方。
 */
public final class BotaniaFloatingFlowerFeedingHook implements IItemFeedingHook {

    @Override
    public boolean isAvailable() {
        return Mods.BOTANIA.isLoaded();
    }

    @Override
    public boolean supports(IRecipeHandler handler) {
        return handler instanceof RecipeHandlerFloatingFlowers;
    }
}
