package moe.takochan.webnei.exporter.domain.dataset;

import lombok.Getter;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.dataset.model.DatasetRow;

/**
 * dataset 基础数据领域模型。
 */
@Getter
public final class DatasetExportModel implements IExportModel {

    /**
     * 模型类型标识，供 bundle writer 选择具体映射逻辑。
     */
    public static final String TYPE = "dataset";

    /**
     * 本次导出的 dataset 行。
     */
    private final DatasetRow dataset;

    public DatasetExportModel(DatasetRow dataset) {
        this.dataset = dataset;
    }

    @Override
    public String type() {
        return TYPE;
    }
}
