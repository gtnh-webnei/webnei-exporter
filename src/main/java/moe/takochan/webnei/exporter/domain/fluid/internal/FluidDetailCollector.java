package moe.takochan.webnei.exporter.domain.fluid.internal;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import moe.takochan.webnei.exporter.domain.fluid.hook.FluidHookRegistry;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidRow;

/** 使用 Forge Fluid/FluidStack API 采集流体基础字段。 */
public final class FluidDetailCollector {

    private final FluidHookRegistry fluidHooks = new FluidHookRegistry();

    public FluidRow collect(String datasetId, FluidIdentity identity, FluidStack stack) {
        Fluid fluid = stack.getFluid();
        FluidRow row = new FluidRow(
            datasetId,
            identity.getFluidId(),
            identity.getModId(),
            identity.getRegistryName(),
            value(stack.getUnlocalizedName()),
            value(stack.getLocalizedName()),
            fluid.getLuminosity(stack),
            fluid.getDensity(stack),
            fluid.getTemperature(stack),
            fluid.getViscosity(stack),
            fluid.isGaseous(stack));
        fluidHooks.enrich(stack, row);
        return row;
    }

    private static String value(String value) {
        return value == null ? "" : value;
    }
}
