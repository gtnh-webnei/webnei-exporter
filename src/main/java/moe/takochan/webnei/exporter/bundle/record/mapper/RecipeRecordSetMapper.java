package moe.takochan.webnei.exporter.bundle.record.mapper;

import java.util.Collections;
import java.util.List;

import moe.takochan.webnei.exporter.bundle.record.BundleRecordSet;
import moe.takochan.webnei.exporter.bundle.record.BundleRecordSetSpec;
import moe.takochan.webnei.exporter.bundle.record.IBundleRecordSetMapper;
import moe.takochan.webnei.exporter.domain.recipe.RecipeExportModel;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryRow;

public final class RecipeRecordSetMapper implements IBundleRecordSetMapper<RecipeExportModel> {

    private static final BundleRecordSetSpec<RecipeCategoryRow> RECIPE_CATEGORY = BundleRecordSetSpec
        .<RecipeCategoryRow>recordSet("recipe_category", 120)
        .field("dataset_id", RecipeCategoryRow::getDatasetId)
        .field("category_id", RecipeCategoryRow::getCategoryId)
        .field("display_name", RecipeCategoryRow::getDisplayName)
        .field("mod_id", RecipeCategoryRow::getModId);

    @Override
    public Class<RecipeExportModel> modelType() {
        return RecipeExportModel.class;
    }

    @Override
    public List<BundleRecordSet> recordSets(RecipeExportModel model) {
        return Collections.singletonList(RECIPE_CATEGORY.records(model.getCategories()));
    }
}
