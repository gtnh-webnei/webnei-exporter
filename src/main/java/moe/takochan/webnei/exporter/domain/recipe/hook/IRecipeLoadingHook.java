package moe.takochan.webnei.exporter.domain.recipe.hook;

import java.util.List;

import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.engine.hook.IExportHook;

/**
 * 配方加载钩子：让 mod 专门负责"如何把某个 NEI handler 加载成可读配方"。
 *
 * <p>
 * 通用 loader 只读取 handler 已有的配方页面 / 用 handler 自报的 overlayIdentifier 触发加载。某些 handler 需要 mod 内部知识才能可靠拿到全量配方，这种情况由
 * mod 实现一个 hook 接管 load。
 */
public interface IRecipeLoadingHook extends IExportHook {

    /** 该 hook 是否能加载指定 handler；不能时由通用 loader 处理。 */
    boolean supports(IRecipeHandler handler);

    /**
     * 加载并返回可枚举配方的 handler 实例列表。
     *
     * <p>
     * 通常返回一个；某些 mod 一次只能产出一段配方，需要多次加载，可返回多个。返回的每个实例都应满足 {@code numRecipes() > 0}，否则由上层视为无配方。
     */
    List<IRecipeHandler> load(IRecipeHandler handler);
}
