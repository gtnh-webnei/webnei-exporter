package moe.takochan.webnei.exporter.domain.item.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** nei_item_panel_entry 表行：NEI item panel 展示入口和当前顺序。 */
@Getter
@RequiredArgsConstructor
public final class NeiItemPanelEntryRow {

    /** 所属 dataset ID。 */
    private final String datasetId;

    /** 面板入口对应的 item_variant ID。 */
    private final String itemVariantId;

    /** 当前 ItemList.items 顺序中的展示索引。 */
    private final int panelIndex;

    /** NEI 可折叠集合 ID；当前未解析时为空。 */
    private final String collapsibleCollectionId;

    /** 折叠状态下该入口是否仍可见。 */
    private final boolean visibleWhenCollapsed;
}
