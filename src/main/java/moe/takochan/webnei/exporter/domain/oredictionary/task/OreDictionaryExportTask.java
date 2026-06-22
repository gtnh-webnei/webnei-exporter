package moe.takochan.webnei.exporter.domain.oredictionary.task;

import moe.takochan.webnei.exporter.domain.dataset.store.DatasetDomainStore;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;
import moe.takochan.webnei.exporter.domain.oredictionary.internal.ForgeOreDictionarySource;
import moe.takochan.webnei.exporter.domain.oredictionary.internal.OreDictionaryDomainData;
import moe.takochan.webnei.exporter.domain.oredictionary.internal.OreDictionaryRegistrar;
import moe.takochan.webnei.exporter.domain.oredictionary.store.OreDictionaryDomainStore;
import moe.takochan.webnei.exporter.engine.task.ExportTaskContext;
import moe.takochan.webnei.exporter.engine.task.IExportTask;

/** ore_dictionary 数据域导出任务。 */
public final class OreDictionaryExportTask implements IExportTask {

    public static final String ID = "ore-dictionary-export";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String labelKey() {
        return "webnei.task.ore_dictionary";
    }

    @Override
    public void execute(ExportTaskContext context) {
        String datasetId = context.store(DatasetDomainStore.class)
            .data()
            .datasetId();
        ItemDomainStore itemStore = context.store(ItemDomainStore.class);

        OreDictionaryDomainData data = new OreDictionaryDomainData(datasetId);
        OreDictionaryRegistrar registrar = new OreDictionaryRegistrar(data, itemStore);
        new ForgeOreDictionarySource(registrar).collect();

        OreDictionaryDomainStore oreStore = new OreDictionaryDomainStore(data, registrar);
        context.register(OreDictionaryDomainStore.class, oreStore);
    }
}
