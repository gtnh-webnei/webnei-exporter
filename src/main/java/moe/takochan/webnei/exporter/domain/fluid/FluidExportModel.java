package moe.takochan.webnei.exporter.domain.fluid;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidBlockRow;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidContainerRow;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidRow;

/** fluid 数据域中间模型，包含 fluid、fluid_container、fluid_block 三类行。 */
@Getter
public final class FluidExportModel implements IExportModel {

    /** 模型类型标识，供 bundle writer 选择具体映射逻辑。 */
    public static final String TYPE = "fluid";

    /** registry fluid 维度的基础行。 */
    private final List<FluidRow> fluids;

    /** 流体容器关联行。 */
    private final List<FluidContainerRow> containers;

    /** 流体方块关联行。 */
    private final List<FluidBlockRow> blocks;

    public FluidExportModel(List<FluidRow> fluids, List<FluidContainerRow> containers, List<FluidBlockRow> blocks) {
        this.fluids = Collections.unmodifiableList(fluids);
        this.containers = Collections.unmodifiableList(containers);
        this.blocks = Collections.unmodifiableList(blocks);
    }

    @Override
    public String type() {
        return TYPE;
    }
}
