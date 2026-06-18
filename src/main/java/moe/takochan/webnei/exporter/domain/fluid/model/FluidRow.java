package moe.takochan.webnei.exporter.domain.fluid.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/** fluid 表行：registry fluid 维度的基础字段。 */
@Getter
@RequiredArgsConstructor
public final class FluidRow {

    /** 所属 dataset ID。 */
    private final String datasetId;

    /** fluid 稳定 ID，通常为 modid:name。 */
    private final String fluidId;

    /** fluid 所属 mod id。 */
    private final String modId;

    /** Forge registry name 的本地名称部分。 */
    private final String registryName;

    /** fluid 暴露的未本地化名称。 */
    private final String unlocalizedName;

    /** 当前语言环境下的显示名称，保留 Minecraft 格式码。 */
    private final String displayName;

    /** 由 fluid hook 补充的化学式；未知时为空。 */
    @Setter
    private String chemicalExpression = "";

    /** 流体发光等级。 */
    private final int luminosity;

    /** 流体密度。 */
    private final int density;

    /** 流体温度。 */
    private final int temperature;

    /** 流体粘度。 */
    private final int viscosity;

    /** 是否为气体。 */
    private final boolean gaseous;
}
