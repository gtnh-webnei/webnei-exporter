package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 一个配方格子的位置与候选条目观察值；role 由 registrar 根据 NEI 显示规则决定。 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class RecipeSlotObservation {

    /** NEI 配方页面内相对 x（已折算 yShift 前的水平坐标）。 */
    private final int x;

    /** NEI 配方页面内相对 y（已折算 handler yShift 进入统一坐标系）。 */
    private final int y;

    /** 该格子的候选条目，按 NEI 候选数组顺序排列。 */
    private final List<RecipeCandidateObservation> candidates;

    static RecipeSlotObservation of(int x, int y, List<RecipeCandidateObservation> candidates) {
        return new RecipeSlotObservation(x, y, Collections.unmodifiableList(candidates));
    }
}
