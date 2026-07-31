package moe.takochan.webnei.exporter.domain.recipe.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** One ordered item candidate in a recipe slot. */
@Getter
@RequiredArgsConstructor
public final class RecipeSlotCandidateRow {

    private final String datasetId;
    private final String recipeId;
    private final String slotKey;
    private final int candidateOrder;
    private final String itemVariantId;
    private final int amount;
}
