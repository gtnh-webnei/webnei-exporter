package moe.takochan.webnei.exporter.domain.aspect.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** aspect 表行：Thaumcraft Aspect registry 的定义。 */
@Getter
@RequiredArgsConstructor
public final class AspectRow {

    private final String datasetId;
    private final String aspectId;
    private final String itemVariantId;
    private final String displayName;
    private final String description;
    private final boolean primal;
    private final int color;
    private final int blend;
    private final String chatColor;
    private final int registryOrder;
}
