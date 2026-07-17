package moe.takochan.webnei.exporter.domain.recipe.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** One ordered tooltip fragment bound to a complete recipe slot candidate key. */
@Getter
@RequiredArgsConstructor
public final class RecipeCandidateTooltipFragmentRow {

    private final String datasetId;
    private final String recipeId;
    private final String slotKey;
    private final int candidateOrder;
    private final int fragmentOrder;
    private final String stateKey;
    private final String textValue;
}
