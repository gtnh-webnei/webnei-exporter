package moe.takochan.webnei.exporter.domain.recipe;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryCatalystRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryRow;

@Getter
public final class RecipeExportModel implements IExportModel {

    public static final String TYPE = "recipe";

    private final List<RecipeCategoryRow> categories;
    private final List<RecipeCategoryCatalystRow> catalysts;

    public RecipeExportModel(List<RecipeCategoryRow> categories, List<RecipeCategoryCatalystRow> catalysts) {
        this.categories = Collections.unmodifiableList(categories);
        this.catalysts = Collections.unmodifiableList(catalysts);
    }

    @Override
    public String type() {
        return TYPE;
    }
}
