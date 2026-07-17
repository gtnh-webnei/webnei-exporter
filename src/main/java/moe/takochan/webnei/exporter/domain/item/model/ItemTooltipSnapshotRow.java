package moe.takochan.webnei.exporter.domain.item.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** item_tooltip_snapshot 表行：特定键态下的 item variant tooltip 快照。 */
@Getter
@RequiredArgsConstructor
public final class ItemTooltipSnapshotRow {

    private final String datasetId;
    private final String itemVariantId;
    private final String tooltipType;
    private final String keyState;
    private final String tooltipText;
}
