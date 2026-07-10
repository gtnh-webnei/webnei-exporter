package moe.takochan.webnei.exporter.domain.aspect;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.aspect.model.AspectComponentRow;
import moe.takochan.webnei.exporter.domain.aspect.model.AspectItemRow;
import moe.takochan.webnei.exporter.domain.aspect.model.AspectRow;

/** aspect 数据域中间模型。 */
@Getter
public final class AspectExportModel implements IExportModel {

    public static final String TYPE = "aspect";

    private final List<AspectRow> aspects;
    private final List<AspectComponentRow> components;
    private final List<AspectItemRow> itemAspects;

    public AspectExportModel(List<AspectRow> aspects, List<AspectComponentRow> components,
        List<AspectItemRow> itemAspects) {
        this.aspects = Collections.unmodifiableList(aspects);
        this.components = Collections.unmodifiableList(components);
        this.itemAspects = Collections.unmodifiableList(itemAspects);
    }

    @Override
    public String type() {
        return TYPE;
    }
}
