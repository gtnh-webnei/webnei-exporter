package moe.takochan.webnei.exporter.domain.recipe.internal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** One ordered item candidate observed in a recipe slot. */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class RecipeCandidateObservation {

    private final String itemVariantId;
    private final int amount;
}
