package moe.takochan.webnei.exporter.domain.recipe.hook;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** One tooltip line appended for an active recipe slot candidate. */
@Getter
@RequiredArgsConstructor
public final class RecipeTooltipFragmentObservation {

    private final String stateKey;
    private final String textValue;
}
