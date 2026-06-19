package moe.takochan.webnei.exporter.domain.asset.internal;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import moe.takochan.webnei.exporter.domain.asset.store.AssetDomainStore;
import moe.takochan.webnei.exporter.domain.fluid.store.FluidDomainStore;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;

/** 从已注册 item/fluid domain store 生成最小 asset 行。 */
public final class AssetSource {

    public void collect(AssetDomainStore assetStore, ItemDomainStore itemStore, FluidDomainStore fluidStore) {
        for (Map.Entry<String, ItemStack> entry : itemStore.stacks()
            .entrySet()) {
            assetStore.registerItemIcon(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, FluidStack> entry : fluidStore.stacks()
            .entrySet()) {
            assetStore.registerFluidIcon(entry.getKey(), entry.getValue());
        }
    }
}
