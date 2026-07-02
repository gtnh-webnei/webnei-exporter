package moe.takochan.webnei.exporter.domain.recipe.internal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 一个格子里的单个候选；已通过 item store 解析成稳定 item_variant_id 与 amount。 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class RecipeCandidateObservation {

    /** 目标域：当前只产出 item；fluid 后续由 hook 补。 */
    private final String targetDomain;

    /** 目标 ID：item 时是 item_variant_id；fluid 时是 fluid_id。 */
    private final String targetId;

    /** 候选条目数量。 */
    private final int amount;
}
