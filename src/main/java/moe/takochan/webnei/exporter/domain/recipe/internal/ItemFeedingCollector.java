package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import codechicken.nei.ItemList;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.IUsageHandler;

/**
 * 对一批「需要喂物品才能枚举配方」的 NEI handler，做一次 NEI 全量物品遍历喂入。
 *
 * <p>
 * 这类 handler 没有 {@code loadCraftingRecipes(String outputId)} 全量分支，只在喂入具体物品时现场判断该物品能否命中自己的
 * 配方表。{@code ItemList.items} 只遍历一次，每个物品依次喂给所有待喂 handler 的 {@code getRecipeHandler("item", stack)} /
 * {@code getUsageHandler("item", stack)}，避免每个 handler 各自重复遍历全量物品。
 *
 * <p>
 * 每次喂入返回一个只含匹配该物品的新实例（NEI {@code newInstance} 语义），多数为空。同一配方会在查合成 / 查用途两侧、不同物品下
 * 重复出现，靠下游 {@link RecipeDomainData} 的内容哈希去重收敛。
 */
public final class ItemFeedingCollector {

    /** NEI 查合成 / 查用途的物品 id；{@code getRecipeHandler/getUsageHandler} 默认实现据此分发到喂物品分支。 */
    private static final String ITEM_OUTPUT_ID = "item";

    /** 一个待喂 handler：原始 handler 加它对应的分类身份，以及喂出的非空结果实例。 */
    public static final class FeedTarget {

        private final RecipeCategoryIdentity identity;
        private final IRecipeHandler handler;
        private final List<IRecipeHandler> loaded = new ArrayList<>();

        FeedTarget(RecipeCategoryIdentity identity, IRecipeHandler handler) {
            this.identity = identity;
            this.handler = handler;
        }

        public RecipeCategoryIdentity getIdentity() {
            return identity;
        }

        public List<IRecipeHandler> getLoaded() {
            return loaded;
        }
    }

    private final List<FeedTarget> targets = new ArrayList<>();

    /** 登记一个待喂 handler。 */
    public void add(RecipeCategoryIdentity identity, IRecipeHandler handler) {
        targets.add(new FeedTarget(identity, handler));
    }

    public boolean isEmpty() {
        return targets.isEmpty();
    }

    /**
     * 遍历一次 NEI 全量物品，把每个物品喂给所有待喂 handler，结果累积到各 {@link FeedTarget}。
     *
     * @return 待喂 handler 列表，每项带其喂出的非空实例
     */
    public List<FeedTarget> feed() {
        for (ItemStack stack : ItemList.items) {
            if (stack == null || stack.getItem() == null) {
                continue;
            }
            for (FeedTarget target : targets) {
                feedOne(target, stack);
            }
        }
        return targets;
    }

    private static void feedOne(FeedTarget target, ItemStack stack) {
        if (target.handler instanceof ICraftingHandler) {
            addIfHasRecipes(target.loaded, feedCrafting((ICraftingHandler) target.handler, stack));
        }
        if (target.handler instanceof IUsageHandler) {
            addIfHasRecipes(target.loaded, feedUsage((IUsageHandler) target.handler, stack));
        }
    }

    private static IRecipeHandler feedCrafting(ICraftingHandler handler, ItemStack stack) {
        try {
            return handler.getRecipeHandler(ITEM_OUTPUT_ID, stack);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static IRecipeHandler feedUsage(IUsageHandler handler, ItemStack stack) {
        try {
            return handler.getUsageHandler(ITEM_OUTPUT_ID, stack);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static void addIfHasRecipes(List<IRecipeHandler> out, IRecipeHandler handler) {
        if (handler != null && safeNumRecipes(handler) > 0) {
            out.add(handler);
        }
    }

    private static int safeNumRecipes(IRecipeHandler handler) {
        try {
            return handler.numRecipes();
        } catch (Throwable ignored) {
            return 0;
        }
    }
}
