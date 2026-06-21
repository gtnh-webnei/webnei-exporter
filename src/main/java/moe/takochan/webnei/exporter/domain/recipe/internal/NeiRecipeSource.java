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
 * 本类只负责「遍历来源」这一件事，不解析分类身份、不去重、不建行——拿到一个 handler 就交给
 * {@link RecipeRegistrar#register}，由 domain 内部决定解析成什么；当前解析为 category，后续会展开 recipe。
 *
 * <p>
 * NEI 把 handler 分别登记在四个静态列表里，对应「查合成」和「查用途」两个方向，各自又分普通与序列化两种：
 * <ul>
 * <li>{@link GuiCraftingRecipe#craftinghandlers} —— 查合成
 * <li>{@link GuiCraftingRecipe#serialCraftingHandlers} —— 查合成（序列化注册）
 * <li>{@link GuiUsageRecipe#usagehandlers} —— 查用途
 * <li>{@link GuiUsageRecipe#serialUsageHandlers} —— 查用途（序列化注册）
 * </ul>
 * 四个列表都会被扫描，确保普通与序列化注册的查合成、查用途 handler 都能进入 recipe registrar。
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
                this.registrar.register(handler);
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
