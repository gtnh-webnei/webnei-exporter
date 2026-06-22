package moe.takochan.webnei.exporter.domain.recipe.hook;

import java.util.List;

import moe.takochan.webnei.exporter.engine.hook.HookRegistry;

/** 汇总所有 {@link IRecipeCategorySkipHook}，任一钩子命中即视为该分类应被跳过。 */
public final class RecipeCategoryHookRegistry {

    private final List<IRecipeCategorySkipHook> hooks;

    public RecipeCategoryHookRegistry() {
        this.hooks = HookRegistry.get(IRecipeCategorySkipHook.class);
    }

    /** 任一钩子判定跳过则返回 true。 */
    public boolean shouldSkip(String categoryId) {
        for (IRecipeCategorySkipHook hook : hooks) {
            if (hook.shouldSkip(categoryId)) {
                return true;
            }
        }
        return false;
    }
}
