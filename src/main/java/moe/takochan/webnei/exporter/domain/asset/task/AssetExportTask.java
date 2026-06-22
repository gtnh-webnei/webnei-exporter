package moe.takochan.webnei.exporter.domain.asset.task;

import moe.takochan.webnei.exporter.domain.asset.internal.AssetDomainData;
import moe.takochan.webnei.exporter.domain.asset.internal.AssetRegistrar;
import moe.takochan.webnei.exporter.domain.asset.internal.AssetSource;
import moe.takochan.webnei.exporter.domain.asset.store.AssetDomainStore;
import moe.takochan.webnei.exporter.domain.dataset.store.DatasetDomainStore;
import moe.takochan.webnei.exporter.domain.fluid.store.FluidDomainStore;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;
import moe.takochan.webnei.exporter.domain.recipe.store.RecipeDomainStore;
import moe.takochan.webnei.exporter.engine.task.ExportTaskContext;
import moe.takochan.webnei.exporter.engine.task.IExportTask;

public final class AssetExportTask implements IExportTask {

    public static final String ID = "asset-export";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String labelKey() {
        return "webnei.task.assets";
    }

    @Override
    public void execute(ExportTaskContext context) {
        String datasetId = context.store(DatasetDomainStore.class)
            .data()
            .datasetId();
        ItemDomainStore itemStore = context.store(ItemDomainStore.class);
        FluidDomainStore fluidStore = context.store(FluidDomainStore.class);
        RecipeDomainStore recipeStore = context.store(RecipeDomainStore.class);

        AssetDomainData data = new AssetDomainData(datasetId);
        AssetRegistrar registrar = new AssetRegistrar(data);
        new AssetSource(registrar, itemStore, fluidStore, recipeStore).collect();

        AssetDomainStore store = new AssetDomainStore(data, registrar);
        context.register(AssetDomainStore.class, store);
    }
}
