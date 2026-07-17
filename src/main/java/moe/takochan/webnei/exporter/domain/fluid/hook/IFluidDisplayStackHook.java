package moe.takochan.webnei.exporter.domain.fluid.hook;

import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.engine.hook.IExportHook;

/**
 * 流体显示物识别钩子：判定某个 NEI 配方里的 ItemStack 本质上是"用 item 占位的流体"（如 GT Display_Fluid）。
 *
 * <p>
 * NEI 的 StackInfo 能把流体容器、流体显示占位物都解析为 FluidStack，但它无法区分"装液体的容器物品"和"只用于显示的占位 item"。
 * 前者在配方里仍是 item 候选（同时应注册到 fluid_container 表），后者本质上是流体候选。这里用 hook 让 mod 提供
 * "显示占位"判定，命中即视为流体候选。
 */
public interface IFluidDisplayStackHook extends IExportHook {

    /**
     * 判定该 stack 是否仅用于在 NEI 中显示流体（而非真实流体容器物品）。
     *
     * @param stack 候选 ItemStack
     * @return true 表示该 stack 应作为流体候选导出
     */
    boolean isFluidDisplay(ItemStack stack);

    /** Structured recipe presentation type for displays recognized by this hook. */
    String presentationType();

    /** Structured amount unit for displays recognized by this hook. */
    String amountUnit();
}
