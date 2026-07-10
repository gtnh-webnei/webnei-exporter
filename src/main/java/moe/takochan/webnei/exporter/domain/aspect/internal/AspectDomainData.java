package moe.takochan.webnei.exporter.domain.aspect.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.aspect.AspectExportModel;
import moe.takochan.webnei.exporter.domain.aspect.model.AspectComponentRow;
import moe.takochan.webnei.exporter.domain.aspect.model.AspectItemRow;
import moe.takochan.webnei.exporter.domain.aspect.model.AspectRow;
import moe.takochan.webnei.exporter.engine.store.IDomainData;

/** aspect domain 的内部有序结果集。 */
public final class AspectDomainData implements IDomainData {

    private static final char KEY_SEPARATOR = '\u0000';

    private final Map<String, AspectRow> aspects = new LinkedHashMap<>();
    private final Map<String, AspectComponentRow> components = new LinkedHashMap<>();
    private final Map<String, AspectItemRow> itemAspects = new LinkedHashMap<>();

    void putAspect(AspectRow row) {
        aspects.putIfAbsent(row.getAspectId(), row);
    }

    void putComponent(AspectComponentRow row) {
        components.putIfAbsent(componentKey(row.getAspectId(), row.getComponentIndex()), row);
    }

    void putItemAspect(AspectItemRow row) {
        itemAspects.putIfAbsent(itemAspectKey(row.getItemVariantId(), row.getAspectId()), row);
    }

    @Override
    public IExportModel toExportModel() {
        return new AspectExportModel(
            new ArrayList<>(aspects.values()),
            new ArrayList<>(components.values()),
            new ArrayList<>(itemAspects.values()));
    }

    private static String componentKey(String aspectId, int componentIndex) {
        return aspectId + KEY_SEPARATOR + componentIndex;
    }

    private static String itemAspectKey(String itemVariantId, String aspectId) {
        return itemVariantId + KEY_SEPARATOR + aspectId;
    }
}
