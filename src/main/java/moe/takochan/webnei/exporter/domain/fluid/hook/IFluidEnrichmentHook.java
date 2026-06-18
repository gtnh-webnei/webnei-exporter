package moe.takochan.webnei.exporter.domain.fluid.hook;

import net.minecraftforge.fluids.FluidStack;

import moe.takochan.webnei.exporter.domain.fluid.model.FluidRow;
import moe.takochan.webnei.exporter.engine.hook.IExportHook;

/**
 * fluid 字段补充扩展点。
 *
 * <p>
 * 在 fluid store 完成基础字段采集后、写入 row 前被调用，用于从 mod 特有 API 提取额外字段写入 FluidRow。
 */
public interface IFluidEnrichmentHook extends IExportHook {

    /**
     * 从 FluidStack 中提取额外信息并写入 row 的可选字段。
     *
     * @param stack 当前正在注册的 FluidStack
     * @param row   基础字段已填充的 fluid 行，可直接修改
     */
    void enrich(FluidStack stack, FluidRow row);
}
