package moe.takochan.webnei.exporter.domain.recipe.internal;

import codechicken.nei.recipe.IRecipeHandler;

public final class RecipeRegistrar {

    private final RecipeDomainData data;

    public RecipeRegistrar(RecipeDomainData data) {
        this.data = data;
    }

    public void register(IRecipeHandler handler) {
        data.register(handler);
    }
}
