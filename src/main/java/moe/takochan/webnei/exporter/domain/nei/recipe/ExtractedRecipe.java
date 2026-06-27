package moe.takochan.webnei.exporter.domain.nei.recipe;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 实验性 NEI 探测/抽取代码；当前未接入 ExportPlan.ALL 的正式导出流程。
 * 请勿在正式导出链路中引用，仅供参考。
 * 一个 NEI recipe 页面的视觉事实抽取摘要。
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ExtractedRecipe {

    /** 所属 handler 的稳定 key。 */
    private final String handlerKey;

    /** handler 内 recipe 顺序索引。 */
    private final int recipeIndex;

    /** 基于 slot 坐标和候选 item 生成的 recipe 指纹。 */
    private final String fingerprint;

    /** ingredient slot 数量。 */
    private final int ingredientCount;

    /** result slot 数量。 */
    private final int resultCount;

    /** other slot 数量。 */
    private final int otherCount;

    /** 抽取状态，例如 standard、partial、error。 */
    private final String status;

    /** 状态补充说明或错误原因。 */
    private final String reason;
}
