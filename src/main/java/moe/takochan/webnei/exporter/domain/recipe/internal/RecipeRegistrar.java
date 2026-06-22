package moe.takochan.webnei.exporter.domain.recipe.internal;

import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeCategoryHookRegistry;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryCatalystRow;
import moe.takochan.webnei.exporter.engine.store.IDomainRegistrar;

/**
 * recipe domain 的注册编排入口：把 {@link NeiRecipeSource} 送来的每个 handler 解析成分类身份，过滤被钩子跳过的分类，
 * 再把首次出现的分类及其 catalyst 行写入 {@link RecipeDomainData}。
 */
public final class RecipeRegistrar implements IDomainRegistrar {

    private final String datasetId;
    private final RecipeCategoryIdentityResolver identityResolver = new RecipeCategoryIdentityResolver();
    private final RecipeCategoryHookRegistry recipeCategoryHooks = new RecipeCategoryHookRegistry();
    private final RecipeCatalystCollector catalystCollector;
    private final RecipeDomainData data;

    public RecipeRegistrar(RecipeDomainData data, String datasetId, ItemDomainStore itemStore) {
        this.data = data;
        this.datasetId = datasetId;
        this.catalystCollector = new RecipeCatalystCollector(itemStore);
    }

    public void register(IRecipeHandler handler) {
        RecipeCategoryIdentity identity = identityResolver.resolve(handler);
        if (recipeCategoryHooks.shouldSkip(identity.getCategoryId())) {
            return;
        }
        // 仅在分类首次出现时采集 catalyst，避免重复 handler 重复建行。
        if (data.putIdentity(identity)) {
            for (RecipeCategoryCatalystRow row : catalystCollector
                .collect(datasetId, identity.getCategoryId(), handler)) {
                data.putCatalyst(row);
            }
        }
    }
}
