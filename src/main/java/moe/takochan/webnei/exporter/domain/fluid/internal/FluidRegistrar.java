package moe.takochan.webnei.exporter.domain.fluid.internal;

import net.minecraftforge.fluids.Fluid;

public final class FluidRegistrar {

    private final FluidDomainData data;

    public FluidRegistrar(FluidDomainData data) {
        this.data = data;
    }

    public void register(Fluid fluid) {
        data.getOrRegisterFluid(fluid);
    }
}
