package moe.takochan.webnei.exporter.model.dataset;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import moe.takochan.webnei.exporter.model.IExportModel;

/** dataset/mod 基础数据领域模型。 */
@Getter
public final class DatasetModExportModel implements IExportModel {

    /** 模型类型标识，供 bundle writer 选择具体映射逻辑。 */
    public static final String TYPE = "dataset-mod";

    /** 本次导出的 dataset 行。 */
    private final DatasetRow dataset;

    /** 当前运行环境中已加载 mod 的快照行。 */
    private final List<ModRow> mods;

    public DatasetModExportModel(DatasetRow dataset, List<ModRow> mods) {
        this.dataset = dataset;
        this.mods = Collections.unmodifiableList(mods);
    }

    @Override
    public String type() {
        return TYPE;
    }
}
