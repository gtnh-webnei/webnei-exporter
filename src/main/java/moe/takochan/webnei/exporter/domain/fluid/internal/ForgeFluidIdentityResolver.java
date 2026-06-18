package moe.takochan.webnei.exporter.domain.fluid.internal;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

/** 基于 Forge FluidRegistry 解析 fluid 稳定身份。 */
public final class ForgeFluidIdentityResolver {

    /**
     * 解析流体身份。
     *
     * <p>
     * 使用 {@link FluidRegistry#getDefaultFluidName(Fluid)}，保证 modId:name 与 Forge 流体默认注册名一致。
     */
    public FluidIdentity resolve(Fluid fluid) {
        String fluidId = FluidRegistry.getDefaultFluidName(fluid);
        int separator = fluidId.indexOf(':');
        return new FluidIdentity(fluidId, fluidId.substring(0, separator), fluidId.substring(separator + 1));
    }
}
