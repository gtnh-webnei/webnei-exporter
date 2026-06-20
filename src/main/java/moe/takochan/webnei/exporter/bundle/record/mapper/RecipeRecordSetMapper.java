package moe.takochan.webnei.exporter.bundle.record.mapper;

import java.util.Collections;
import java.util.List;

import moe.takochan.webnei.exporter.bundle.record.BundleRecordSet;
import moe.takochan.webnei.exporter.bundle.record.IBundleRecordSetMapper;
import moe.takochan.webnei.exporter.domain.recipe.RecipeExportModel;

/** recipe export model 的记录集映射。 */
public final class RecipeRecordSetMapper implements IBundleRecordSetMapper<RecipeExportModel> {

    @Override
    public Class<RecipeExportModel> modelType() {
        return RecipeExportModel.class;
    }

    @Override
    public List<BundleRecordSet> recordSets(RecipeExportModel model) {
        return Collections.emptyList();
    }
}
