package moe.takochan.webnei.exporter.domain.fluid.internal;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import moe.takochan.webnei.exporter.domain.fluid.hook.FluidHookRegistry;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidBlockRow;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidContainerRow;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidRow;
import moe.takochan.webnei.exporter.domain.fluid.store.FluidDisplayResolution;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;
import moe.takochan.webnei.exporter.engine.store.IDomainRegistrar;

public final class FluidRegistrar implements IDomainRegistrar {

    private final String datasetId;
    private final ForgeFluidIdentityResolver identityResolver = new ForgeFluidIdentityResolver();
    private final FluidDetailCollector detailCollector = new FluidDetailCollector();
    private final FluidBlockCollector blockCollector;
    private final FluidContainerCollector containerCollector;
    private final FluidHookRegistry hooks = new FluidHookRegistry();
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

    /**
     * 把 NEI 配方里的"流体显示占位 ItemStack"识别并解析成稳定流体引用。
     *
     * <p>
     * 必须由至少一个 {@link moe.takochan.webnei.exporter.domain.fluid.hook.IFluidDisplayStackHook} 命中才算显示物；
     * 没有任何 hook 命中时返回 null，调用方应把该 stack 当作 item 候选（流体容器物品也走该路径，再由
     * {@link #registerContainer} 顺手补充 fluid_container 行）。
     */
    public FluidDisplayResolution tryAsFluidDisplay(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return null;
        }
        if (!hooks.isFluidDisplay(stack)) {
            return null;
        }
        FluidStack fluidStack = FluidStackResolver.resolve(stack);
        if (fluidStack == null || fluidStack.getFluid() == null) {
            return null;
        }
        FluidRow row = getOrRegisterFluid(fluidStack.getFluid());
        return new FluidDisplayResolution(row.getFluidId(), fluidStack.amount);
    }
}
