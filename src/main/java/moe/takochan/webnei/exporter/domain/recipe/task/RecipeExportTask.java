package moe.takochan.webnei.exporter.domain.recipe.task;

import moe.takochan.webnei.exporter.domain.dataset.store.DatasetDomainStore;
import moe.takochan.webnei.exporter.domain.recipe.internal.NeiRecipeSource;
import moe.takochan.webnei.exporter.domain.recipe.store.RecipeDomainStore;
import moe.takochan.webnei.exporter.engine.task.ExportTaskContext;
import moe.takochan.webnei.exporter.engine.task.IExportTask;

/** recipe 数据域导出任务。 */
public final class RecipeExportTask implements IExportTask {

    public static final String ID = "recipe-export";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String labelKey() {
        return "webnei.task.recipes";
    }

    @Override
    public void execute(ExportTaskContext context) {
        String datasetId = context.store(DatasetDomainStore.class)
            .datasetId();

        RecipeDomainStore store = new RecipeDomainStore(datasetId);
        new NeiRecipeSource().collect(store);
        context.register(RecipeDomainStore.class, store);
    }
}
