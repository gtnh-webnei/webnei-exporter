package moe.takochan.webnei.exporter.domain.aspect.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** aspect_item 表行：普通 item variant 的最终有效 Aspect amount。 */
@Getter
@RequiredArgsConstructor
public final class AspectItemRow {

    private final String datasetId;
    private final String itemVariantId;
    private final String aspectId;
    private final int amount;
}
