package moe.takochan.webnei.exporter.domain.recipe.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** recipe 表行：分类下的一个具体配方页面。 */
@Getter
@RequiredArgsConstructor
public final class RecipeRow {

    private final String datasetId;
    private final String recipeId;
    private final String categoryId;
    private final int displayOrder;
}
