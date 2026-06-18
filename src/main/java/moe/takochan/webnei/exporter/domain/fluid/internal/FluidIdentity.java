package moe.takochan.webnei.exporter.domain.fluid.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Forge fluid registry 中解析出的稳定流体身份。 */
@Getter
@RequiredArgsConstructor
public final class FluidIdentity {

    /** fluid 稳定 ID，使用 Forge default fluid name。 */
    private final String fluidId;

    /** fluid 所属 mod id。 */
    private final String modId;

    /** Forge fluid registry name 的本地名称部分。 */
    private final String registryName;
}
