package moe.takochan.webnei.exporter.domain.nei.recipe;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 实验性 NEI 探测/抽取代码；当前未接入 ExportPlan.ALL 的正式导出流程。
 * 请勿在正式导出链路中引用，仅供参考。
 * 一个 PositionedStack slot 展开的具体 ItemStack candidate。
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ExtractedCandidate {

    /** 所属 handler 的稳定 key。 */
    private final String handlerKey;

    /** handler 内 recipe 顺序索引。 */
    private final int recipeIndex;

    /** slot 来源：ingredient、result 或 other。 */
    private final String stackSource;

    /** 同一来源列表内的 slot 顺序索引。 */
    private final int stackIndex;

    /** 同一 slot 内 candidate 顺序索引。 */
    private final int candidateIndex;

    /** candidate ItemStack 的 registry item ID。 */
    private final String itemId;

    /** candidate ItemStack 原始 damage/meta 值。 */
    private final int damage;

    /** candidate ItemStack 堆叠数量。 */
    private final int stackSize;

    /** candidate ItemStack 当前显示名称。 */
    private final String displayName;
}
