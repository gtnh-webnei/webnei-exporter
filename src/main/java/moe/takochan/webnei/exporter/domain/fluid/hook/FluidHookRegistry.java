package moe.takochan.webnei.exporter.domain.fluid.hook;

import java.util.List;

import net.minecraftforge.fluids.FluidStack;

import moe.takochan.webnei.exporter.domain.fluid.model.FluidRow;
import moe.takochan.webnei.exporter.engine.hook.HookRegistry;

/** 持有所有可用的 fluid enrichment hook。 */
public final class FluidHookRegistry {

    private final List<IFluidEnrichmentHook> enrichmentHooks;

    /** 创建 fluid hook registry。 */
    public FluidHookRegistry() {
        this.enrichmentHooks = HookRegistry.get(IFluidEnrichmentHook.class);
    }

    /** 依次调用所有已注册 enrichment hook 补充 row 字段。 */
    public void enrich(FluidStack stack, FluidRow row) {
        for (IFluidEnrichmentHook hook : enrichmentHooks) {
            hook.enrich(stack, row);
        }
    }
}
