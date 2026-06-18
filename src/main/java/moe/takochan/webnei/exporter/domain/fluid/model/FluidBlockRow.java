package moe.takochan.webnei.exporter.domain.fluid.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** fluid_block 表行：流体与游戏内 Block 的关联。 */
@Getter
@RequiredArgsConstructor
public final class FluidBlockRow {

    /** 所属 dataset ID。 */
    private final String datasetId;

    /** 对应的 fluid 稳定 ID。 */
    private final String fluidId;

    /** 流体在世界中的 Block 对应 item variant ID。 */
    private final String itemVariantId;
}
