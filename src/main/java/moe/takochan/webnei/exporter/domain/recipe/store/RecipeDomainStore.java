package moe.takochan.webnei.exporter.domain.recipe.store;

import moe.takochan.webnei.exporter.domain.recipe.internal.RecipeDomainData;
import moe.takochan.webnei.exporter.domain.recipe.internal.RecipeRegistrar;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/** recipe domain 在 exporter store 中的入口：对外暴露结果集 {@link RecipeDomainData} 与注册器 {@link RecipeRegistrar}。 */
public final class RecipeDomainStore implements IDomainStore<RecipeDomainData, RecipeRegistrar> {

    private final RecipeDomainData data;
    private final RecipeRegistrar registrar;

    public RecipeDomainStore(RecipeDomainData data, RecipeRegistrar registrar) {
        this.data = data;
        this.registrar = registrar;
    }

    @Override
    public RecipeDomainData data() {
        return data;
    }

    @Override
    public RecipeRegistrar registrar() {
        return registrar;
    }
}
