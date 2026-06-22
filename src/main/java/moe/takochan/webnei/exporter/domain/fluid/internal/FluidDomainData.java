package moe.takochan.webnei.exporter.domain.fluid.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraftforge.fluids.FluidStack;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.fluid.FluidExportModel;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidBlockRow;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidContainerRow;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidRow;
import moe.takochan.webnei.exporter.engine.store.IDomainData;

/**
 * fluid domain store 的内部结果集。
 *
 * <p>
 * 该类只持有 fluid/block/container 结果集；注册编排职责由 FluidRegistrar 负责。
 */
public final class FluidDomainData implements IDomainData {

    private final Map<String, FluidRow> fluids = new LinkedHashMap<>();
    private final Map<String, FluidStack> stacks = new LinkedHashMap<>();
    private final Map<String, FluidContainerRow> containers = new LinkedHashMap<>();
    private final Map<String, FluidBlockRow> blocks = new LinkedHashMap<>();

    FluidRow findFluid(String fluidId) {
        return fluids.get(fluidId);
    }

    void putFluid(String fluidId, FluidRow row, FluidStack stack) {
        fluids.putIfAbsent(fluidId, row);
        stacks.putIfAbsent(fluidId, stack);
    }

    /** 返回已注册 fluid 对应的代表 FluidStack。 */
    public Map<String, FluidStack> stacks() {
        return Collections.unmodifiableMap(stacks);
    }

    void putBlock(FluidBlockRow row) {
        if (row != null) {
            blocks.putIfAbsent(row.getFluidId() + '\u0000' + row.getItemVariantId(), row);
        }
    }

    void putContainer(FluidContainerRow row) {
        if (row != null) {
            containers.putIfAbsent(row.getFluidId() + '\u0000' + row.getItemVariantId(), row);
        }
    }

    @Override
    public IExportModel toExportModel() {
        return new FluidExportModel(
            new ArrayList<>(fluids.values()),
            new ArrayList<>(containers.values()),
            new ArrayList<>(blocks.values()));
    }
}
