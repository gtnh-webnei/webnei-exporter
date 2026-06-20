package moe.takochan.webnei.exporter.domain.recipe.internal;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.recipe.RecipeExportModel;

/** recipe domain store 的内部数据。 */
public final class RecipeDomainData {

    public IExportModel toExportModel() {
        return new RecipeExportModel();
    }
}
