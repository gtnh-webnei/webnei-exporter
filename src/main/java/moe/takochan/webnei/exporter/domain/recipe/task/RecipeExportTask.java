package moe.takochan.webnei.exporter.domain.recipe.task;

import moe.takochan.webnei.exporter.domain.dataset.store.DatasetDomainStore;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;
import moe.takochan.webnei.exporter.domain.recipe.internal.NeiRecipeSource;
import moe.takochan.webnei.exporter.domain.recipe.internal.RecipeDomainData;
import moe.takochan.webnei.exporter.domain.recipe.internal.RecipeRegistrar;
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
            .data()
            .datasetId();
        ItemDomainStore itemStore = context.store(ItemDomainStore.class);

        RecipeDomainData data = new RecipeDomainData(datasetId);
        RecipeRegistrar registrar = new RecipeRegistrar(data, datasetId, itemStore);
        new NeiRecipeSource(registrar).collect();

        RecipeDomainStore store = new RecipeDomainStore(data, registrar);
        context.register(RecipeDomainStore.class, store);
    }
}
