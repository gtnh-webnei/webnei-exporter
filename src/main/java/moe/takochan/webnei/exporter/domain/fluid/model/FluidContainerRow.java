package moe.takochan.webnei.exporter.domain.fluid.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** fluid_container 表行：流体与容器 item variant 的关联。 */
@Getter
@RequiredArgsConstructor
public final class FluidContainerRow {

    /** 所属 dataset ID。 */
    private final String datasetId;

    /** 被容器承载的 fluid 稳定 ID。 */
    private final String fluidId;

    /** 容器内流体数量，单位 mB。 */
    private final int amount;

    /** 承载该流体的容器 item variant ID。 */
    private final String itemVariantId;
}
