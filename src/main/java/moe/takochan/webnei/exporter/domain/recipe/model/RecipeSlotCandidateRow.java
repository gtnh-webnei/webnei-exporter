package moe.takochan.webnei.exporter.domain.recipe.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** One ordered candidate in a recipe slot. */
@Getter
@RequiredArgsConstructor
public final class RecipeSlotCandidateRow {

    private final String datasetId;
    private final String recipeId;
    private final String slotKey;
    private final int candidateOrder;
    private final String targetDomain;
    private final String targetId;
    private final int amount;
    private final double probability;
    private final String presentationType;
    private final String presentationId;
    private final String amountUnit;
}
