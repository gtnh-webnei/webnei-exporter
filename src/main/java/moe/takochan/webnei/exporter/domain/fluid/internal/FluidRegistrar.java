package moe.takochan.webnei.exporter.domain.fluid.internal;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import moe.takochan.webnei.exporter.domain.fluid.model.FluidBlockRow;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidContainerRow;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidRow;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;
import moe.takochan.webnei.exporter.engine.store.IDomainRegistrar;

public final class FluidRegistrar implements IDomainRegistrar {

    private final String datasetId;
    private final ForgeFluidIdentityResolver identityResolver = new ForgeFluidIdentityResolver();
    private final FluidDetailCollector detailCollector = new FluidDetailCollector();
    private final FluidBlockCollector blockCollector;
    private final FluidContainerCollector containerCollector;
    private final FluidDomainData data;

    public FluidRegistrar(FluidDomainData data, String datasetId, ItemDomainStore itemStore) {
        this.data = data;
        this.datasetId = datasetId;
        this.blockCollector = new FluidBlockCollector(itemStore);
        this.containerCollector = new FluidContainerCollector(itemStore);
    }

    public FluidRow getOrRegisterFluid(Fluid fluid) {
        FluidIdentity identity = identityResolver.resolve(fluid);
        FluidRow existing = data.findFluid(identity.getFluidId());
        if (existing != null) {
            return existing;
        }
        FluidStack stack = new FluidStack(fluid, 1);
        FluidRow row = detailCollector.collect(datasetId, identity, stack);
        data.putFluid(row.getFluidId(), row, stack);
        FluidBlockRow block = blockCollector.collect(datasetId, row.getFluidId(), fluid);
        data.putBlock(block);
        for (FluidContainerRow container : containerCollector.collect(datasetId, row.getFluidId(), fluid)) {
            data.putContainer(container);
        }
        return row;
    }

    public FluidContainerRow registerContainer(ItemStack containerStack) {
        Fluid fluid = containerCollector.resolveFluid(containerStack);
        if (fluid == null) {
            return null;
        }
        FluidRow row = getOrRegisterFluid(fluid);
        FluidContainerRow containerRow = containerCollector.collectOne(datasetId, row.getFluidId(), containerStack);
        data.putContainer(containerRow);
        return containerRow;
    }
}
