package moe.takochan.webnei.exporter.domain.mod;

import lombok.Getter;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.mod.model.DatasetRow;
import moe.takochan.webnei.exporter.domain.mod.model.ModRow;

import java.util.Collections;
import java.util.List;

/** dataset/mod 基础数据领域模型。 */
@Getter
public final class DatasetModExportModel implements IExportModel {

    /** 模型类型标识，供 bundle writer 选择具体映射逻辑。 */
    public static final String TYPE = "mod";

    /** 当前运行环境中已加载 mod 的快照行。 */
    private final List<ModRow> mods;

    public DatasetModExportModel(List<ModRow> mods) {
        this.mods = Collections.unmodifiableList(mods);
    }

    @Override
    public String type() {
        return TYPE;
    }
}
