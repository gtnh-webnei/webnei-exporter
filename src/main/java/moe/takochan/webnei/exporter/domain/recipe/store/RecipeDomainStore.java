package moe.takochan.webnei.exporter.domain.recipe.store;

import java.util.Map;

import net.minecraft.item.ItemStack;

import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.recipe.internal.RecipeDomainData;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

public final class RecipeDomainStore implements IDomainStore {

    private final RecipeDomainData data;

    public RecipeDomainStore(String datasetId) {
        this.data = new RecipeDomainData(datasetId);
    }

    public void register(IRecipeHandler handler) {
        data.register(handler);
    }

    public Map<String, ItemStack> categoryIconStacks() {
        return data.categoryIconStacks();
    }

    @Override
    public IExportModel toExportModel() {
        return data.toExportModel();
    }
}
