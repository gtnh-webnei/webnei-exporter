package moe.takochan.webnei.exporter.domain.recipe.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * recipe_slot_layout 表行：category/handler 级固定格子定义。
 *
 * <p>
 * slot_key 是 category 内稳定格子标识，由 role + x + y 派生；同 category 下相同 role 与坐标只产生一条 layout。
 */
@Getter
@RequiredArgsConstructor
public final class RecipeSlotLayoutRow {

    private final String datasetId;
    private final String categoryId;
    private final String slotKey;
    private final String role;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int displayOrder;
}
