package moe.takochan.webnei.exporter.domain.asset.internal;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import moe.takochan.webnei.exporter.domain.fluid.store.FluidDomainStore;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;
import moe.takochan.webnei.exporter.domain.recipe.store.RecipeDomainStore;

public final class AssetSource {

    private final AssetRegistrar registrar;
    private final ItemDomainStore itemStore;
    private final FluidDomainStore fluidStore;
    private final RecipeDomainStore recipeStore;

    public AssetSource(AssetRegistrar registrar, ItemDomainStore itemStore, FluidDomainStore fluidStore,
        RecipeDomainStore recipeStore) {
        this.registrar = registrar;
        this.itemStore = itemStore;
        this.fluidStore = fluidStore;
        this.recipeStore = recipeStore;
    }

    public void collect() {
        for (Map.Entry<String, ItemStack> entry : this.itemStore.data()
            .stacks()
            .entrySet()) {
            this.registrar.registerItemIcon(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, FluidStack> entry : this.fluidStore.data()
            .stacks()
            .entrySet()) {
            this.registrar.registerFluidIcon(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ItemStack> entry : this.recipeStore.data()
            .categoryIconStacks()
            .entrySet()) {
            this.registrar.registerRecipeCategoryIcon(entry.getKey(), entry.getValue());
        }
    }
}
