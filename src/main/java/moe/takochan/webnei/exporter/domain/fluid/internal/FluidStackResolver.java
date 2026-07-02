package moe.takochan.webnei.exporter.domain.fluid.internal;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import codechicken.nei.recipe.StackInfo;

/**
 * 把一个 ItemStack 解析成它所代表的 FluidStack 的中性工具。
 *
 * <p>
 * StackInfo 会统一识别 Forge 简单容器、IFluidContainerItem、GT fluid display 和 AE2FC fluid stack 表示物。 该解析只读不写，既被容器采集复用，也被
 * 流体显示占位物解析复用，不属于任何单一职责，故独立于 collector/registrar 存在。
 */
public final class FluidStackResolver {

    private FluidStackResolver() {}

    /** 解析单个 stack 承载/代表的流体栈（含 amount）；无法解析为流体时返回 {@code null}。 */
    public static FluidStack resolve(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return null;
        }
        FluidStack fluidStack = StackInfo.getFluid(stack);
        if (fluidStack == null || fluidStack.getFluid() == null) {
            return null;
        }
        return fluidStack;
    }
}
