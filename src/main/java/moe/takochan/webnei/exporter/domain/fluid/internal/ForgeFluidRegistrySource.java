package moe.takochan.webnei.exporter.domain.fluid.internal;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import moe.takochan.webnei.exporter.domain.fluid.store.FluidDomainStore;

/**
 * 读取 Forge FluidRegistry。
 *
 * <p>
 * {@code FluidRegistry.getRegisteredFluids()} 是流体的权威来源。每个流体注册到 store 时，会自动挂接其方块和容器关系。
 */
public final class ForgeFluidRegistrySource {

    public void collect(FluidDomainStore store) {
        for (Fluid fluid : FluidRegistry.getRegisteredFluids()
            .values()) {
            store.getOrRegisterFluid(fluid);
        }
    }
}
