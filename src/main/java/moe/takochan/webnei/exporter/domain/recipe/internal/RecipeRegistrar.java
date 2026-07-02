package moe.takochan.webnei.exporter.domain.recipe.internal;

import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.domain.fluid.store.FluidDomainStore;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;
import moe.takochan.webnei.exporter.domain.recipe.hook.ItemFeedingHookRegistry;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeCategoryHookRegistry;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeLoadingHookRegistry;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeSlotSourceHookRegistry;
import moe.takochan.webnei.exporter.domain.recipe.internal.ItemFeedingCollector.FeedTarget;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryCatalystRow;
import moe.takochan.webnei.exporter.engine.store.IDomainRegistrar;

/**
 * recipe domain 的注册编排入口：把 {@link NeiRecipeSource} 送来的每个 handler 解析成分类身份，过滤被跳过钩子命中的分类，
 * 再把首次出现的分类的 catalyst 与 recipe visual facts 写入 {@link RecipeDomainData}。
 *
 * <p>
 * 普通 handler 在 {@link #register} 阶段即时展开。需要喂物品的 handler 只先暂存，待 {@link NeiRecipeSource} 扫描完所有
 * handler 后，由 {@link #finishFeeding} 对 NEI 全量物品做一次遍历统一喂入，避免每个待喂 handler 各自重复遍历全量物品。
 */
public final class RecipeRegistrar implements IDomainRegistrar {

    private final String datasetId;
    private final RecipeCategoryIdentityResolver identityResolver = new RecipeCategoryIdentityResolver();
    private final RecipeCategoryHookRegistry recipeCategoryHooks = new RecipeCategoryHookRegistry();
    private final ItemFeedingHookRegistry feedingHooks = new ItemFeedingHookRegistry();
    private final RecipeCatalystCollector catalystCollector;
    private final RecipeHandlerLoader handlerLoader;
    private final RecipeVisualFactCollector visualCollector;
    private final ItemFeedingCollector feedingCollector = new ItemFeedingCollector();
    private final RecipeDomainData data;

    public RecipeRegistrar(RecipeDomainData data, String datasetId, ItemDomainStore itemStore,
        FluidDomainStore fluidStore) {
        this.data = data;
        this.datasetId = datasetId;
        this.catalystCollector = new RecipeCatalystCollector(itemStore);
        this.handlerLoader = new RecipeHandlerLoader(new RecipeLoadingHookRegistry());
        this.visualCollector = new RecipeVisualFactCollector(itemStore, fluidStore, new RecipeSlotSourceHookRegistry());
    }

    public void register(IRecipeHandler handler) {
        RecipeCategoryIdentity identity = identityResolver.resolve(handler);
        if (recipeCategoryHooks.shouldSkip(identity.getCategoryId())) {
            return;
        }
        if (!data.putIdentity(identity)) {
            return;
        }
        for (RecipeCategoryCatalystRow row : catalystCollector.collect(datasetId, identity.getCategoryId(), handler)) {
            data.putCatalyst(row);
        }
        if (feedingHooks.needsFeeding(handler)) {
            feedingCollector.add(identity, handler);
            return;
        }
        for (IRecipeHandler loaded : handlerLoader.load(handler)) {
            collectVisuals(identity, loaded);
        }
    }

    /**
     * 扫描收尾：对所有暂存的待喂 handler 做一次 NEI 全量物品遍历喂入，再展开各自喂出的配方实例。
     *
     * <p>
     * 必须在 {@link NeiRecipeSource} 把所有 handler 都 {@link #register} 之后调用一次。
     */
    public void finishFeeding() {
        if (feedingCollector.isEmpty()) {
            return;
        }
        for (FeedTarget target : feedingCollector.feed()) {
            for (IRecipeHandler loaded : target.getLoaded()) {
                collectVisuals(target.getIdentity(), loaded);
            }
        }
    }

    /** 逐配方页面采集 visual facts 写入 data。 */
    private void collectVisuals(RecipeCategoryIdentity identity, IRecipeHandler loaded) {
        int total = safeNumRecipes(loaded);
        for (int recipeIndex = 0; recipeIndex < total; recipeIndex++) {
            RecipeVisualObservation observation = visualCollector.collect(loaded, recipeIndex, identity);
            if (observation == null) {
                continue;
            }
            data.registerVisual(identity, observation);
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
