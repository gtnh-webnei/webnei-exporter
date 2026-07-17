package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeTooltipFragmentObservation;

/** One slot candidate with a canonical target and an independent presentation carrier. */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class RecipeCandidateObservation {

    private final String targetDomain;
    private final String targetId;
    private final int amount;
    private final String presentationType;
    private final String presentationId;
    private final String amountUnit;
    private final double probability;
    private final List<RecipeTooltipFragmentObservation> fragments;
}
