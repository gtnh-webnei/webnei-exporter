package moe.takochan.webnei.exporter.domain.recipe.store;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.recipe.internal.RecipeDomainData;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/** recipe domain store — recipe domain 的跨 domain 交互边界。 */
public final class RecipeDomainStore implements IDomainStore {

    private final RecipeDomainData data;

    public RecipeDomainStore(String datasetId) {
        this.data = new RecipeDomainData();
    }

    @Override
    public IExportModel toExportModel() {
        return data.toExportModel();
    }
}
