package moe.takochan.webnei.exporter.domain.recipe.internal;

import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.domain.fluid.store.FluidDomainStore;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeCategoryHookRegistry;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryCatalystRow;
import moe.takochan.webnei.exporter.engine.store.IDomainRegistrar;

/**
 * recipe domain 的注册编排入口：把 {@link NeiRecipeSource} 送来的每个 handler 解析成分类身份，过滤被分类跳过钩子命中的分类，
 * 再把首次出现分类的 catalyst 与可直接枚举的 recipe visual facts 写入 {@link RecipeDomainData}。
 */
public final class RecipeRegistrar implements IDomainRegistrar {

    private final String datasetId;
    private final RecipeCategoryIdentityResolver identityResolver = new RecipeCategoryIdentityResolver();
    private final RecipeCategoryHookRegistry recipeCategoryHooks = new RecipeCategoryHookRegistry();
    private final RecipeCatalystCollector catalystCollector;
    private final RecipeHandlerLoader handlerLoader = new RecipeHandlerLoader();
    private final RecipeVisualFactCollector visualCollector;
    private final RecipeDomainData data;

    public RecipeRegistrar(RecipeDomainData data, String datasetId, ItemDomainStore itemStore,
        FluidDomainStore fluidStore) {
        this.data = data;
        this.datasetId = datasetId;
        this.catalystCollector = new RecipeCatalystCollector(itemStore);
        this.visualCollector = new RecipeVisualFactCollector(itemStore, fluidStore);
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
        for (IRecipeHandler loaded : handlerLoader.load(handler)) {
            collectVisuals(identity, loaded);
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
