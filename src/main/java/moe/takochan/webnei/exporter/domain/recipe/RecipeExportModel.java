package moe.takochan.webnei.exporter.domain.recipe;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCandidateTooltipFragmentRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryCatalystRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeSlotCandidateRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeSlotLayoutRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeTooltipRegionRow;

/** Immutable export model for the recipe domain. */
@Getter
public final class RecipeExportModel implements IExportModel {

    public static final String TYPE = "recipe";

    private final List<RecipeCategoryRow> categories;
    private final List<RecipeCategoryCatalystRow> catalysts;
    private final List<RecipeRow> recipes;
    private final List<RecipeSlotLayoutRow> slotLayouts;
    private final List<RecipeSlotCandidateRow> slotCandidates;
    private final List<RecipeCandidateTooltipFragmentRow> candidateTooltipFragments;
    private final List<RecipeTooltipRegionRow> tooltipRegions;

    public RecipeExportModel(List<RecipeCategoryRow> categories, List<RecipeCategoryCatalystRow> catalysts,
        List<RecipeRow> recipes, List<RecipeSlotLayoutRow> slotLayouts, List<RecipeSlotCandidateRow> slotCandidates,
        List<RecipeCandidateTooltipFragmentRow> candidateTooltipFragments,
        List<RecipeTooltipRegionRow> tooltipRegions) {
        this.categories = Collections.unmodifiableList(categories);
        this.catalysts = Collections.unmodifiableList(catalysts);
        this.recipes = Collections.unmodifiableList(recipes);
        this.slotLayouts = Collections.unmodifiableList(slotLayouts);
        this.slotCandidates = Collections.unmodifiableList(slotCandidates);
        this.candidateTooltipFragments = Collections.unmodifiableList(candidateTooltipFragments);
        this.tooltipRegions = Collections.unmodifiableList(tooltipRegions);
    }

    @Override
    public String type() {
        return TYPE;
    }
}
