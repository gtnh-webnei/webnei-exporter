package moe.takochan.webnei.exporter.domain.recipe.hook;

import java.util.List;

import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.engine.hook.HookRegistry;

/** 汇总所有 {@link IRecipeLoadingHook}；按注册顺序找第一个支持当前 handler 的 hook。 */
public final class RecipeLoadingHookRegistry {

    private final List<IRecipeLoadingHook> hooks;

    public RecipeLoadingHookRegistry() {
        this.hooks = HookRegistry.get(IRecipeLoadingHook.class);
    }

    /** 找到能处理该 handler 的 hook；没有时返回 null，由通用 loader 处理。 */
    public IRecipeLoadingHook find(IRecipeHandler handler) {
        for (IRecipeLoadingHook hook : hooks) {
            if (hook.supports(handler)) {
                return hook;
            }
        }
        return null;
    }
}
