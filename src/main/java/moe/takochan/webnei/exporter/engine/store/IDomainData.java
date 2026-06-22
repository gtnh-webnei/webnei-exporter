package moe.takochan.webnei.exporter.engine.store;

import moe.takochan.webnei.exporter.domain.IExportModel;

/**
 * domain data 标准接口。
 *
 * <p>
 * 表示一个 domain 的结果集与导出读取能力。
 */
public interface IDomainData {

    /** 将当前结果集转为最终导出模型。 */
    IExportModel toExportModel();
}
