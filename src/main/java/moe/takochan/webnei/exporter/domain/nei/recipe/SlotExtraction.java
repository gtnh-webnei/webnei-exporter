package moe.takochan.webnei.exporter.domain.nei.recipe;

import java.util.Collections;
import java.util.List;

import lombok.Getter;

/** 一次 recipe visual facts 抽取的完整结果集合。 */
@Getter
public final class SlotExtraction {

    /** 每个 NEI handler 的加载和抽取摘要。 */
    private final List<ExtractedHandler> handlers;

    /** 每个已加载 recipe 的抽取摘要。 */
    private final List<ExtractedRecipe> recipes;

    /** 每个 recipe 中 ingredient/result/other slot 的位置和候选统计。 */
    private final List<ExtractedStack> stacks;

    /** 每个 slot 展开的具体 ItemStack candidate。 */
    private final List<ExtractedCandidate> candidates;

    SlotExtraction(List<ExtractedHandler> handlers, List<ExtractedRecipe> recipes, List<ExtractedStack> stacks,
        List<ExtractedCandidate> candidates) {
        this.handlers = Collections.unmodifiableList(handlers);
        this.recipes = Collections.unmodifiableList(recipes);
        this.stacks = Collections.unmodifiableList(stacks);
        this.candidates = Collections.unmodifiableList(candidates);
    }
}
