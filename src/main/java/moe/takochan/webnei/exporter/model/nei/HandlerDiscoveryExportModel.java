package moe.takochan.webnei.exporter.model.nei;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import moe.takochan.webnei.exporter.model.IExportModel;
import moe.takochan.webnei.exporter.nei.scan.NeiHandlerEntry;

/** NEI handler/category 扫描验证模型。 */
@Getter
public final class HandlerDiscoveryExportModel implements IExportModel {

    /** 模型类型标识，供 bundle writer 选择具体映射逻辑。 */
    public static final String TYPE = "handler-discovery";

    /** 运行时扫描到的 NEI handler 条目，保留 descriptor 和原始 handler 实例。 */
    private final List<NeiHandlerEntry> entries;

    public HandlerDiscoveryExportModel(List<NeiHandlerEntry> entries) {
        this.entries = Collections.unmodifiableList(entries);
    }

    @Override
    public String type() {
        return TYPE;
    }
}
