package moe.takochan.webnei.exporter.domain.recipe.hook;

import java.util.ArrayList;
import java.util.List;

import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.engine.hook.HookRegistry;

/** 汇总所有 {@link IRecipeSlotSourceHook}；为某 handler 收集所有命中钩子产出的额外格子。 */
public final class RecipeSlotSourceHookRegistry {

    private final List<IRecipeSlotSourceHook> hooks;

    public RecipeSlotSourceHookRegistry() {
        this.hooks = HookRegistry.get(IRecipeSlotSourceHook.class);
    }

    /** 该 handler 是否有任一钩子声明可补充额外格子。 */
    public boolean hasSource(IRecipeHandler handler) {
        for (IRecipeSlotSourceHook hook : hooks) {
            if (hook.supports(handler)) {
                return true;
            }
        }
        return false;
    }

    /** 收集所有命中钩子对该 handler 第 {@code recipeIndex} 个配方页面产出的额外格子。 */
    public List<ExtraRecipeSlot> extractSlots(IRecipeHandler handler, int recipeIndex) {
        List<ExtraRecipeSlot> out = new ArrayList<>();
        for (IRecipeSlotSourceHook hook : hooks) {
            if (!hook.supports(handler)) {
                continue;
            }
            List<ExtraRecipeSlot> slots = safeExtract(hook, handler, recipeIndex);
            if (slots != null) {
                out.addAll(slots);
            }
        }
        return out;
    }

    private static List<ExtraRecipeSlot> safeExtract(IRecipeSlotSourceHook hook, IRecipeHandler handler,
        int recipeIndex) {
        try {
            return hook.extractSlots(handler, recipeIndex);
        } catch (Throwable ignored) {
            return null;
        }
    }
}
