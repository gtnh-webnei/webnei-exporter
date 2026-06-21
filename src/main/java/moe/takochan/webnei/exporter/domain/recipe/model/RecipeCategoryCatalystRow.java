package moe.takochan.webnei.exporter.domain.recipe.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** recipe_category_catalyst 表行：能打开某个配方分类的物品（NEI catalyst）。 */
@Getter
@RequiredArgsConstructor
public final class RecipeCategoryCatalystRow {

    private final String datasetId;
    private final String categoryId;
    private final String itemVariantId;
    private final int displayOrder;
}
