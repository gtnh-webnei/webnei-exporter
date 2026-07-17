package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeTooltipRegionObservation;

/** One recipe page's ordered NEI visual facts. */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class RecipeVisualObservation {

    private final List<RecipeSlotObservation> inputs;
    private final RecipeSlotObservation result;
    private final List<RecipeSlotObservation> others;
    private final List<RecipeSlotObservation> extraInputs;
    private final List<RecipeSlotObservation> extraOutputs;
    private final List<RecipeTooltipRegionObservation> regions;

    static RecipeVisualObservation of(List<RecipeSlotObservation> inputs, RecipeSlotObservation result,
        List<RecipeSlotObservation> others, List<RecipeSlotObservation> extraInputs,
        List<RecipeSlotObservation> extraOutputs, List<RecipeTooltipRegionObservation> regions) {
        return new RecipeVisualObservation(
            Collections.unmodifiableList(inputs),
            result,
            Collections.unmodifiableList(others),
            Collections.unmodifiableList(extraInputs),
            Collections.unmodifiableList(extraOutputs),
            Collections.unmodifiableList(regions));
    }
}
