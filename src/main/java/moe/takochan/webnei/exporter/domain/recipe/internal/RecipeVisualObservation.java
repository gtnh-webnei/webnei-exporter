package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** One recipe page's ordered NEI visual facts. */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class RecipeVisualObservation {

    private final List<RecipeSlotObservation> inputs;
    private final RecipeSlotObservation result;
    private final List<RecipeSlotObservation> others;

    static RecipeVisualObservation of(List<RecipeSlotObservation> inputs, RecipeSlotObservation result,
        List<RecipeSlotObservation> others) {
        return new RecipeVisualObservation(
            Collections.unmodifiableList(inputs),
            result,
            Collections.unmodifiableList(others));
    }
}
