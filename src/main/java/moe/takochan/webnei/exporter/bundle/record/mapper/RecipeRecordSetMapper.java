package moe.takochan.webnei.exporter.bundle.record.mapper;

import java.util.Arrays;
import java.util.List;

import moe.takochan.webnei.exporter.bundle.record.BundleRecordSet;
import moe.takochan.webnei.exporter.bundle.record.BundleRecordSetSpec;
import moe.takochan.webnei.exporter.bundle.record.IBundleRecordSetMapper;
import moe.takochan.webnei.exporter.domain.recipe.RecipeExportModel;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryCatalystRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryRow;

public final class RecipeRecordSetMapper implements IBundleRecordSetMapper<RecipeExportModel> {

    private static final BundleRecordSetSpec<RecipeCategoryRow> RECIPE_CATEGORY = BundleRecordSetSpec
        .<RecipeCategoryRow>recordSet("recipe_category", 120)
        .field("dataset_id", RecipeCategoryRow::getDatasetId)
        .field("category_id", RecipeCategoryRow::getCategoryId)
        .field("display_name", RecipeCategoryRow::getDisplayName)
        .field("mod_id", RecipeCategoryRow::getModId);

    private static final BundleRecordSetSpec<RecipeCategoryCatalystRow> RECIPE_CATEGORY_CATALYST = BundleRecordSetSpec
        .<RecipeCategoryCatalystRow>recordSet("recipe_category_catalyst", 125)
        .field("dataset_id", RecipeCategoryCatalystRow::getDatasetId)
        .field("category_id", RecipeCategoryCatalystRow::getCategoryId)
        .field("item_variant_id", RecipeCategoryCatalystRow::getItemVariantId)
        .field("display_order", RecipeCategoryCatalystRow::getDisplayOrder);

    @Override
    public Class<RecipeExportModel> modelType() {
        return RecipeExportModel.class;
    }

    @Override
    public List<BundleRecordSet> recordSets(RecipeExportModel model) {
        return Arrays.asList(
            RECIPE_CATEGORY.records(model.getCategories()),
            RECIPE_CATEGORY_CATALYST.records(model.getCatalysts()));
    }
}
