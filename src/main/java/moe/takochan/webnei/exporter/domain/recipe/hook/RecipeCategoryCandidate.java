package moe.takochan.webnei.exporter.domain.recipe.hook;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class RecipeCategoryCandidate {

    private final String categoryId;
    private final String modId;
    private final String name;
}
