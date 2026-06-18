package moe.takochan.webnei.exporter.domain.fluid.hook;

import java.util.List;

import net.minecraftforge.fluids.FluidStack;

import moe.takochan.webnei.exporter.domain.fluid.model.FluidRow;
import moe.takochan.webnei.exporter.engine.hook.HookRegistry;

/** 持有所有可用的 fluid enrichment hook，由 fluid store 在注册 fluid 时调用。 */
public final class FluidHookRegistry {

    private final List<IFluidEnrichmentHook> hooks;

    /** 创建 fluid hook registry。 */
    public FluidHookRegistry() {
        this.hooks = HookRegistry.get(IFluidEnrichmentHook.class);
    }

    /**
     * 依次调用所有已注册 hook 补充 row 字段。
     *
     * @param stack 当前正在注册的 FluidStack
     * @param row   基础字段已填充的 fluid 行
     */
    public void enrich(FluidStack stack, FluidRow row) {
        for (IFluidEnrichmentHook hook : hooks) {
            hook.enrich(stack, row);
        }
    }
}
