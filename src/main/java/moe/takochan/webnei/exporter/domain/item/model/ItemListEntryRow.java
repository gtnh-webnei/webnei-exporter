package moe.takochan.webnei.exporter.domain.item.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** item_list_entry 表行：前端物品列表展示入口和排序。 */
@Getter
@RequiredArgsConstructor
public final class ItemListEntryRow {

    /** 所属 dataset ID。 */
    private final String datasetId;

    /** 列表入口对应的 item_variant ID。 */
    private final String itemVariantId;

    /** 展示顺序索引。 */
    private final int listIndex;
}
