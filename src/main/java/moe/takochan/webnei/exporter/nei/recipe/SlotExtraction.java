package moe.takochan.webnei.exporter.nei.recipe;

import java.util.Collections;
import java.util.List;

public final class SlotExtraction {

    public final List<ExtractedHandler> handlers;
    public final List<ExtractedRecipe> recipes;
    public final List<ExtractedStack> stacks;
    public final List<ExtractedCandidate> candidates;

    SlotExtraction(List<ExtractedHandler> handlers, List<ExtractedRecipe> recipes, List<ExtractedStack> stacks,
        List<ExtractedCandidate> candidates) {
        this.handlers = Collections.unmodifiableList(handlers);
        this.recipes = Collections.unmodifiableList(recipes);
        this.stacks = Collections.unmodifiableList(stacks);
        this.candidates = Collections.unmodifiableList(candidates);
    }
}
