package moe.takochan.webnei.exporter.domain.fluid.internal;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * 读取 Forge FluidRegistry。
 *
 * <p>
 * {@code FluidRegistry.getRegisteredFluids()} 是流体的权威来源。每个流体注册到 data 时，会自动挂接其方块和容器关系。
 */
public final class ForgeFluidRegistrySource {

    private final FluidRegistrar registrar;

    public ForgeFluidRegistrySource(FluidRegistrar registrar) {
        this.registrar = registrar;
    }

    public void collect() {
        for (Fluid fluid : FluidRegistry.getRegisteredFluids()
            .values()) {
            this.registrar.getOrRegisterFluid(fluid);
        }
    }
}
