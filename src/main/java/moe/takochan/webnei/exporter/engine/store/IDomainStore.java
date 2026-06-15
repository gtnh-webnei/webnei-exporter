package moe.takochan.webnei.exporter.engine.store;

import moe.takochan.webnei.exporter.domain.IExportModel;

/**
 * domain store 标准接口。
 *
 * <p>
 * 唯一约束：提供最终导出模型给引擎统一收集。
 * 各 domain 的具体读写能力由自己的 store 类定义，不受此接口约束。
 */
public interface IDomainStore {

    /** 将当前数据集转为最终导出模型，由引擎统一收集。 */
    IExportModel toExportModel();
}
