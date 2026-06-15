package moe.takochan.webnei.exporter.model;

import java.util.Collections;
import java.util.List;

import lombok.Getter;

/** workflow 收集完成后交给 bundle writer 的领域模型集合。 */
@Getter
public final class ExportModelSet {

    /** 当前导出计划 ID，用于 bundle writer 在缺少 dataset 模型时决定默认输出命名。 */
    private final String planId;

    /** 本次 workflow 已收集的领域中间模型，按 step 执行顺序排列。 */
    private final List<IExportModel> models;

    public ExportModelSet(String planId, List<IExportModel> models) {
        this.planId = planId;
        this.models = Collections.unmodifiableList(models);
    }
}
