package moe.takochan.webnei.exporter.domain.recipe.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * recipe_slot_candidate 表行：某个配方在某个格子里的一个候选条目。
 *
 * <p>
 * 一个格子多个可替换候选，就是同一个 (recipe_id, slot_key) 下多行 candidate；candidate_order 按 NEI 候选数组顺序递增。
 */
@Getter
@RequiredArgsConstructor
public final class RecipeSlotCandidateRow {

    private final String datasetId;
    private final String recipeId;
    private final String slotKey;
    private final int candidateOrder;
    private final String targetDomain;
    private final String targetId;
    private final int amount;
    private final double probability;
}
