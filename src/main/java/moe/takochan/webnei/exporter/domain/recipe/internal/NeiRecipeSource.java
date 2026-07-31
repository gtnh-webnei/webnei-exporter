package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.Arrays;
import java.util.List;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.IRecipeHandler;

/**
 * recipe domain 唯一的 NEI 来源：遍历 NEI 已注册的 recipe handler，逐个交给 recipe domain。
 *
 * <p>
 * 本类只负责遍历来源，不解析分类身份、不去重、不建行。NEI 把 handler 分别登记在查合成和查用途两个方向的普通与序列化
 * 静态列表中，四个列表都会被扫描。
 */
public final class NeiRecipeSource {

    private final RecipeRegistrar registrar;

    public NeiRecipeSource(RecipeRegistrar registrar) {
        this.registrar = registrar;
    }

    /** 扫描 NEI 四个 handler 列表，把每个 handler 交给 recipe registrar。 */
    public void collect() {
        for (List<? extends IRecipeHandler> handlers : neiHandlerLists()) {
            for (IRecipeHandler handler : handlers) {
                registrar.register(handler);
            }
        }
    }

    /** NEI 登记 recipe handler 的四个静态列表：查合成 / 查用途，各含普通与序列化注册两种。 */
    private static List<List<? extends IRecipeHandler>> neiHandlerLists() {
        return Arrays.<List<? extends IRecipeHandler>>asList(
            GuiCraftingRecipe.craftinghandlers,
            GuiCraftingRecipe.serialCraftingHandlers,
            GuiUsageRecipe.usagehandlers,
            GuiUsageRecipe.serialUsageHandlers);
    }
}
