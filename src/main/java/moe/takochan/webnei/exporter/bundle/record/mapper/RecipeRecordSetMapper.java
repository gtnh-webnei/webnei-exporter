package moe.takochan.webnei.exporter.bundle.record.mapper;

import java.util.Arrays;
import java.util.List;

import moe.takochan.webnei.exporter.bundle.record.BundleRecordSet;
import moe.takochan.webnei.exporter.bundle.record.BundleRecordSetSpec;
import moe.takochan.webnei.exporter.bundle.record.IBundleRecordSetMapper;
import moe.takochan.webnei.exporter.domain.recipe.RecipeExportModel;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCandidateTooltipFragmentRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryCatalystRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeSlotCandidateRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeSlotLayoutRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeTooltipRegionRow;

public final class RecipeRecordSetMapper implements IBundleRecordSetMapper<RecipeExportModel> {

    private static final BundleRecordSetSpec<RecipeCategoryRow> RECIPE_CATEGORY = BundleRecordSetSpec
        .<RecipeCategoryRow>recordSet("recipe_category", 120)
        .field("dataset_id", RecipeCategoryRow::getDatasetId)
        .field("category_id", RecipeCategoryRow::getCategoryId)
        .field("display_name", RecipeCategoryRow::getDisplayName)
        .field("mod_id", RecipeCategoryRow::getModId)
        .field("canvas_width", RecipeCategoryRow::getCanvasWidth)
        .field("canvas_height", RecipeCategoryRow::getCanvasHeight);

    private static final BundleRecordSetSpec<RecipeCategoryCatalystRow> RECIPE_CATEGORY_CATALYST = BundleRecordSetSpec
        .<RecipeCategoryCatalystRow>recordSet("recipe_category_catalyst", 125)
        .field("dataset_id", RecipeCategoryCatalystRow::getDatasetId)
        .field("category_id", RecipeCategoryCatalystRow::getCategoryId)
        .field("item_variant_id", RecipeCategoryCatalystRow::getItemVariantId)
        .field("display_order", RecipeCategoryCatalystRow::getDisplayOrder);

    private static final BundleRecordSetSpec<RecipeRow> RECIPE = BundleRecordSetSpec.<RecipeRow>recordSet("recipe", 130)
        .field("dataset_id", RecipeRow::getDatasetId)
        .field("recipe_id", RecipeRow::getRecipeId)
        .field("category_id", RecipeRow::getCategoryId)
        .field("display_order", RecipeRow::getDisplayOrder);

    private static final BundleRecordSetSpec<RecipeSlotLayoutRow> RECIPE_SLOT_LAYOUT = BundleRecordSetSpec
        .<RecipeSlotLayoutRow>recordSet("recipe_slot_layout", 135)
        .field("dataset_id", RecipeSlotLayoutRow::getDatasetId)
        .field("category_id", RecipeSlotLayoutRow::getCategoryId)
        .field("slot_key", RecipeSlotLayoutRow::getSlotKey)
        .field("role", RecipeSlotLayoutRow::getRole)
        .field("x", RecipeSlotLayoutRow::getX)
        .field("y", RecipeSlotLayoutRow::getY)
        .field("width", RecipeSlotLayoutRow::getWidth)
        .field("height", RecipeSlotLayoutRow::getHeight)
        .field("display_order", RecipeSlotLayoutRow::getDisplayOrder);

    private static final BundleRecordSetSpec<RecipeSlotCandidateRow> RECIPE_SLOT_CANDIDATE = BundleRecordSetSpec
        .<RecipeSlotCandidateRow>recordSet("recipe_slot_candidate", 140)
        .field("dataset_id", RecipeSlotCandidateRow::getDatasetId)
        .field("recipe_id", RecipeSlotCandidateRow::getRecipeId)
        .field("slot_key", RecipeSlotCandidateRow::getSlotKey)
        .field("candidate_order", RecipeSlotCandidateRow::getCandidateOrder)
        .field("target_domain", RecipeSlotCandidateRow::getTargetDomain)
        .field("target_id", RecipeSlotCandidateRow::getTargetId)
        .field("amount", RecipeSlotCandidateRow::getAmount)
        .field("probability", RecipeSlotCandidateRow::getProbability)
        .field("presentation_type", RecipeSlotCandidateRow::getPresentationType)
        .field("presentation_id", RecipeSlotCandidateRow::getPresentationId)
        .field("amount_unit", RecipeSlotCandidateRow::getAmountUnit);

    private static final BundleRecordSetSpec<RecipeCandidateTooltipFragmentRow> RECIPE_CANDIDATE_TOOLTIP_FRAGMENT = BundleRecordSetSpec
        .<RecipeCandidateTooltipFragmentRow>recordSet("recipe_candidate_tooltip_fragment", 145)
        .field("dataset_id", RecipeCandidateTooltipFragmentRow::getDatasetId)
        .field("recipe_id", RecipeCandidateTooltipFragmentRow::getRecipeId)
        .field("slot_key", RecipeCandidateTooltipFragmentRow::getSlotKey)
        .field("candidate_order", RecipeCandidateTooltipFragmentRow::getCandidateOrder)
        .field("fragment_order", RecipeCandidateTooltipFragmentRow::getFragmentOrder)
        .field("state_key", RecipeCandidateTooltipFragmentRow::getStateKey)
        .field("text_value", RecipeCandidateTooltipFragmentRow::getTextValue);

    private static final BundleRecordSetSpec<RecipeTooltipRegionRow> RECIPE_TOOLTIP_REGION = BundleRecordSetSpec
        .<RecipeTooltipRegionRow>recordSet("recipe_tooltip_region", 150)
        .field("dataset_id", RecipeTooltipRegionRow::getDatasetId)
        .field("recipe_id", RecipeTooltipRegionRow::getRecipeId)
        .field("region_order", RecipeTooltipRegionRow::getRegionOrder)
        .field("region_type", RecipeTooltipRegionRow::getRegionType)
        .field("x", RecipeTooltipRegionRow::getX)
        .field("y", RecipeTooltipRegionRow::getY)
        .field("width", RecipeTooltipRegionRow::getWidth)
        .field("height", RecipeTooltipRegionRow::getHeight)
        .field("state_key", RecipeTooltipRegionRow::getStateKey)
        .field("tooltip_text", RecipeTooltipRegionRow::getTooltipText);

    @Override
    public Class<RecipeExportModel> modelType() {
        return RecipeExportModel.class;
    }

    @Override
    public List<BundleRecordSet> recordSets(RecipeExportModel model) {
        return Arrays.asList(
            RECIPE_CATEGORY.records(model.getCategories()),
            RECIPE_CATEGORY_CATALYST.records(model.getCatalysts()),
            RECIPE.records(model.getRecipes()),
            RECIPE_SLOT_LAYOUT.records(model.getSlotLayouts()),
            RECIPE_SLOT_CANDIDATE.records(model.getSlotCandidates()),
            RECIPE_CANDIDATE_TOOLTIP_FRAGMENT.records(model.getCandidateTooltipFragments()),
            RECIPE_TOOLTIP_REGION.records(model.getTooltipRegions()));
    }
}
