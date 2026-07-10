package moe.takochan.webnei.exporter.domain.aspect.internal;

import moe.takochan.webnei.exporter.domain.aspect.model.AspectComponentRow;
import moe.takochan.webnei.exporter.domain.aspect.model.AspectItemRow;
import moe.takochan.webnei.exporter.domain.aspect.model.AspectRow;
import moe.takochan.webnei.exporter.engine.store.IDomainRegistrar;
import thaumcraft.api.aspects.Aspect;

/** 将 Aspect API 返回的结构化事实写入 aspect domain 数据集。 */
public final class AspectRegistrar implements IDomainRegistrar {

    private final AspectDomainData data;
    private final String datasetId;

    public AspectRegistrar(AspectDomainData data, String datasetId) {
        this.data = data;
        this.datasetId = datasetId;
    }

    void registerDefinition(Aspect aspect, String itemVariantId, int registryOrder) {
        data.putAspect(
            new AspectRow(
                datasetId,
                aspect.getTag(),
                itemVariantId,
                aspect.getName(),
                aspect.getLocalizedDescription(),
                aspect.isPrimal(),
                aspect.getColor(),
                aspect.getBlend(),
                aspect.getChatcolor(),
                registryOrder));
    }

    void registerComponent(String aspectId, int componentIndex, String componentAspectId) {
        data.putComponent(new AspectComponentRow(datasetId, aspectId, componentIndex, componentAspectId));
    }

    void registerItemAspect(String itemVariantId, String aspectId, int amount) {
        data.putItemAspect(new AspectItemRow(datasetId, itemVariantId, aspectId, amount));
    }
}
