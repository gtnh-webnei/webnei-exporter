package moe.takochan.webnei.exporter.domain.fluid.hook;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import moe.takochan.webnei.exporter.domain.fluid.model.FluidRow;
import moe.takochan.webnei.exporter.engine.hook.HookRegistry;

/** 持有所有可用的 fluid 域 hook，按职责分发调用。 */
public final class FluidHookRegistry {

    private final List<IFluidEnrichmentHook> enrichmentHooks;
    private final List<IFluidDisplayStackHook> displayStackHooks;

    /** 创建 fluid hook registry。 */
    public FluidHookRegistry() {
        this.enrichmentHooks = HookRegistry.get(IFluidEnrichmentHook.class);
        this.displayStackHooks = HookRegistry.get(IFluidDisplayStackHook.class);
    }

    /**
     * 依次调用所有已注册 enrichment hook 补充 row 字段。
     *
     * @param stack 当前正在注册的 FluidStack
     * @param row   基础字段已填充的 fluid 行
     */
    public void enrich(FluidStack stack, FluidRow row) {
        for (IFluidEnrichmentHook hook : enrichmentHooks) {
            hook.enrich(stack, row);
        }
    }

    /** 返回第一个命中该流体显示占位 ItemStack 的 hook；未命中时返回 null。 */
    public IFluidDisplayStackHook findFluidDisplayHook(ItemStack stack) {
        for (IFluidDisplayStackHook hook : displayStackHooks) {
            if (hook.isFluidDisplay(stack)) {
                return hook;
            }
        }
        return null;
    }
}
