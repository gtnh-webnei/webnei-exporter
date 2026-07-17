package moe.takochan.webnei.exporter.domain.recipe.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** One ordered non-slot tooltip region bound to a recipe. */
@Getter
@RequiredArgsConstructor
public final class RecipeTooltipRegionRow {

    private final String datasetId;
    private final String recipeId;
    private final int regionOrder;
    private final String regionType;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final String stateKey;
    private final String tooltipText;
}
