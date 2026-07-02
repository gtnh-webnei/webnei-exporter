package moe.takochan.webnei.exporter.hook.gregtech;

import java.util.Collections;
import java.util.List;

import codechicken.nei.recipe.IRecipeHandler;
import cpw.mods.fml.common.Loader;
import gregtech.api.enums.Mods;
import gregtech.nei.GTNEIDefaultHandler;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeLoadingHook;

/**
 * 触发 {@link GTNEIDefaultHandler} 按 RecipeMap 名加载配方。
 *
 * <p>
 * 通用 loader 用 {@link IRecipeHandler#getOverlayIdentifier()} 触发合成查询。GT handler 的
 * overlayIdentifier 返回 {@code recipeCategory.unlocalizedName}，而
 * {@link GTNEIDefaultHandler#loadCraftingRecipes(String, Object...)} 只在 outputId 等于
 * {@code recipeMap.unlocalizedName} 时把缓存 push 进 arecipes。主分类两个名字相等所以能加载；
 * recycling / molding / extruding 这类衍生 RecipeCategory 名不同，于是通用 loader 拿不到配方。
 *
 * <p>
 * 这里直接调 handler 自己的 {@code getRecipeHandler(recipeMap.unlocalizedName, ...)}。 GT 的
 * {@code newInstance()} 会保留同一个 {@code recipeCategory} 字段，所以缓存按 category 维度返回。
 */
public final class GregTechRecipeLoadingHook implements IRecipeLoadingHook {

    @Override
    public boolean isAvailable() {
        return Loader.isModLoaded(Mods.GregTech.ID);
    }

    @Override
    public boolean supports(IRecipeHandler handler) {
        return handler instanceof GTNEIDefaultHandler;
    }

    @Override
    public List<IRecipeHandler> load(IRecipeHandler handler) {
        GTNEIDefaultHandler gt = (GTNEIDefaultHandler) handler;
        String recipeMapKey = gt.getRecipeMap().unlocalizedName;
        IRecipeHandler loaded = gt.getRecipeHandler(recipeMapKey, new Object[0]);
        if (loaded == null || loaded.numRecipes() == 0) {
            return Collections.emptyList();
        }
        return Collections.singletonList(loaded);
    }
}
