package moe.takochan.webnei.exporter.domain.recipe;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryRow;

@Getter
public final class RecipeExportModel implements IExportModel {

    public static final String TYPE = "recipe";

    private final List<RecipeCategoryRow> categories;

    public RecipeExportModel(List<RecipeCategoryRow> categories) {
        this.categories = Collections.unmodifiableList(categories);
    }

    @Override
    public String type() {
        return TYPE;
    }
}
