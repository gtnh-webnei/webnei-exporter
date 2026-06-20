package moe.takochan.webnei.exporter.domain.recipe.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class RecipeCategoryRow {

    private final String datasetId;
    private final String categoryId;
    private final String displayName;
    private final String modId;
}
