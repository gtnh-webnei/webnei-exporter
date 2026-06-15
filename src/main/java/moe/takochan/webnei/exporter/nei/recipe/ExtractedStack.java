package moe.takochan.webnei.exporter.nei.recipe;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 一个 PositionedStack slot 的抽取结果。 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ExtractedStack {

    /** 所属 handler 的稳定 key。 */
    private final String handlerKey;

    /** handler 内 recipe 顺序索引。 */
    private final int recipeIndex;

    /** slot 来源：ingredient、result 或 other。 */
    private final String stackSource;

    /** 同一来源列表内的 slot 顺序索引。 */
    private final int stackIndex;

    /** NEI recipe GUI 内相对 x 坐标。 */
    private final int x;

    /** NEI recipe GUI 内相对 y 坐标。 */
    private final int y;

    /** 该 slot 展开的 candidate 数量。 */
    private final int candidateCount;

    /** 第一个 candidate 的简短文本身份，用于快速检查。 */
    private final String firstCandidate;

    /** 抽取状态，例如 standard、empty。 */
    private final String status;

    /** 状态补充说明或错误原因。 */
    private final String reason;
}
