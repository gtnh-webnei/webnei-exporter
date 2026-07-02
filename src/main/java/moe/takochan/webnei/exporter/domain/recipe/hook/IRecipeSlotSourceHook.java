package moe.takochan.webnei.exporter.domain.recipe.hook;

import java.util.List;

import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.engine.hook.IExportHook;

/**
 * 配方格子来源钩子：从 NEI 标准 {@code getIngredientStacks/getResultStack/getOtherStacks} 之外的来源补充格子。
 *
 * <p>
 * 有些 handler 把部分输入 / 输出放在非 {@code PositionedStack} 的私有结构里（例如 Tinkers' Construct 熔炼体系的
 * {@code getFluidTanks()} 流体罐）。这类数据通用抽取看不到，会导致配方残缺甚至整条丢失。本钩子让 mod 按 handler 把这些来源
 * 解析成带 role 的格子，与通用抽取结果合并。
 *
 * <p>
 * 钩子只负责「从这个 handler 的 recipeIndex 个配方页面读出额外格子」；坐标、role、流体解析由实现按各自 handler 源码确定。
 */
public interface IRecipeSlotSourceHook extends IExportHook {

    /** 该 handler 是否有需要本钩子补充的额外格子来源。 */
    boolean supports(IRecipeHandler handler);

    /**
     * 读出第 {@code recipeIndex} 个配方页面的额外格子。
     *
     * @return 带 role 的额外格子；无则返回空列表
     */
    List<ExtraRecipeSlot> extractSlots(IRecipeHandler handler, int recipeIndex);
}
