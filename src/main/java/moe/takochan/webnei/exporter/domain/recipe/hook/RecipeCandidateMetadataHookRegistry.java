package moe.takochan.webnei.exporter.domain.recipe.hook;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCandidateProtocol;
import moe.takochan.webnei.exporter.engine.hook.HookRegistry;

/** Merges candidate metadata hooks in HookRegistry order. */
public final class RecipeCandidateMetadataHookRegistry {

    private final List<IRecipeCandidateMetadataHook> hooks;

    public RecipeCandidateMetadataHookRegistry() {
        this.hooks = HookRegistry.get(IRecipeCandidateMetadataHook.class);
    }

    public RecipeCandidateMetadata collect(IRecipeHandler handler, int recipeIndex, PositionedStack stack) {
        double probability = RecipeCandidateProtocol.DEFAULT_PROBABILITY;
        List<RecipeTooltipFragmentObservation> fragments = new ArrayList<>();
        for (IRecipeCandidateMetadataHook hook : hooks) {
            if (!hook.supports(handler)) {
                continue;
            }
            RecipeCandidateMetadata metadata = Objects.requireNonNull(
                hook.collect(handler, recipeIndex, stack),
                "Recipe candidate metadata hook returned null: " + hook.getClass()
                    .getName());
            probability = mergeProbability(probability, metadata.getProbability(), hook);
            fragments.addAll(metadata.getFragments());
        }
        return new RecipeCandidateMetadata(probability, fragments);
    }

    private static double mergeProbability(double current, double incoming, IRecipeCandidateMetadataHook hook) {
        double defaultProbability = RecipeCandidateProtocol.DEFAULT_PROBABILITY;
        if (incoming == defaultProbability) {
            return current;
        }
        if (current != defaultProbability && Double.compare(current, incoming) != 0) {
            throw new IllegalStateException(
                "Conflicting recipe candidate probabilities: " + current
                    + " and "
                    + incoming
                    + " from "
                    + hook.getClass()
                        .getName());
        }
        return incoming;
    }
}
