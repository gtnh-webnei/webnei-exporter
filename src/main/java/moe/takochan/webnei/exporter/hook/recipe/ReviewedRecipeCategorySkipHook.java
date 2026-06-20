package moe.takochan.webnei.exporter.hook.recipe;

import java.util.LinkedHashSet;
import java.util.Set;

import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeCategorySkipHook;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeCategoryCandidate;

public final class ReviewedRecipeCategorySkipHook implements IRecipeCategorySkipHook {

    private static final Set<String> SKIPPED_CATEGORY_IDS = skippedCategoryIds();

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public boolean shouldSkip(RecipeCategoryCandidate category) {
        return SKIPPED_CATEGORY_IDS.contains(category.getCategoryId());
    }

    private static Set<String> skippedCategoryIds() {
        Set<String> ids = new LinkedHashSet<>();
        for (ReviewedSkippedRecipeCategory category : ReviewedSkippedRecipeCategory.values()) {
            ids.add(category.getCategoryId());
        }
        return ids;
    }
}
