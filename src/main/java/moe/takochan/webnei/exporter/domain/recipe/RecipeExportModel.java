package moe.takochan.webnei.exporter.domain.recipe;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryCatalystRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeSlotCandidateRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeSlotLayoutRow;

/** recipe 数据域的导出模型；承载 category、catalyst、recipe、slot layout、slot candidate 五类行，构造后不可变。 */
@Getter
public final class RecipeExportModel implements IExportModel {

    public static final String TYPE = "recipe";

    private final List<RecipeCategoryRow> categories;
    private final List<RecipeCategoryCatalystRow> catalysts;
    private final List<RecipeRow> recipes;
    private final List<RecipeSlotLayoutRow> slotLayouts;
    private final List<RecipeSlotCandidateRow> slotCandidates;

    public RecipeExportModel(List<RecipeCategoryRow> categories, List<RecipeCategoryCatalystRow> catalysts,
        List<RecipeRow> recipes, List<RecipeSlotLayoutRow> slotLayouts, List<RecipeSlotCandidateRow> slotCandidates) {
        this.categories = Collections.unmodifiableList(categories);
        this.catalysts = Collections.unmodifiableList(catalysts);
        this.recipes = Collections.unmodifiableList(recipes);
        this.slotLayouts = Collections.unmodifiableList(slotLayouts);
        this.slotCandidates = Collections.unmodifiableList(slotCandidates);
    }

    @Override
    public String type() {
        return TYPE;
    }
}
