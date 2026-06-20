package moe.takochan.webnei.exporter.domain.recipe.hook;

import java.util.List;

import moe.takochan.webnei.exporter.engine.hook.HookRegistry;

public final class RecipeCategoryHookRegistry {

    private final List<IRecipeCategorySkipHook> hooks;

    public RecipeCategoryHookRegistry() {
        this.hooks = HookRegistry.get(IRecipeCategorySkipHook.class);
    }

    public boolean shouldSkip(RecipeCategoryCandidate category) {
        for (IRecipeCategorySkipHook hook : hooks) {
            if (hook.shouldSkip(category)) {
                return true;
            }
        }
        return false;
    }
}
