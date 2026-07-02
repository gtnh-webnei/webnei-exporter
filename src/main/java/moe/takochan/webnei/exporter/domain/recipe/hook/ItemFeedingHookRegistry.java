package moe.takochan.webnei.exporter.domain.recipe.hook;

import java.util.List;

import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.engine.hook.HookRegistry;

/** 汇总所有 {@link IItemFeedingHook}；判断某 handler 是否需要喂物品加载。 */
public final class ItemFeedingHookRegistry {

    private final List<IItemFeedingHook> hooks;

    public ItemFeedingHookRegistry() {
        this.hooks = HookRegistry.get(IItemFeedingHook.class);
    }

    /** 是否有任一 hook 声明该 handler 需要喂物品。 */
    public boolean needsFeeding(IRecipeHandler handler) {
        for (IItemFeedingHook hook : hooks) {
            if (hook.supports(handler)) {
                return true;
            }
        }
        return false;
    }
}
