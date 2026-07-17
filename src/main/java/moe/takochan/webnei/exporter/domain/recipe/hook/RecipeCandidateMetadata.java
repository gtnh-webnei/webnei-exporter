package moe.takochan.webnei.exporter.domain.recipe.hook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCandidateProtocol;

/** Probability and ordered tooltip fragments collected for one active recipe candidate. */
@Getter
public final class RecipeCandidateMetadata {

    private static final RecipeCandidateMetadata DEFAULT = new RecipeCandidateMetadata(
        RecipeCandidateProtocol.DEFAULT_PROBABILITY,
        Collections.<RecipeTooltipFragmentObservation>emptyList());

    private final double probability;
    private final List<RecipeTooltipFragmentObservation> fragments;

    public RecipeCandidateMetadata(double probability, List<RecipeTooltipFragmentObservation> fragments) {
        if (probability < 0.0d || probability > 1.0d) {
            throw new IllegalArgumentException("Recipe candidate probability must be between 0 and 1: " + probability);
        }
        this.probability = probability;
        this.fragments = Collections.unmodifiableList(new ArrayList<>(fragments));
    }

    public static RecipeCandidateMetadata defaults() {
        return DEFAULT;
    }
}
