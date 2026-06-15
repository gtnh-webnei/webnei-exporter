package moe.takochan.webnei.exporter.model;

/** exporter 领域中间模型标记接口；具体 TSV/JSON/SQL 映射由 bundle writer 决定。 */
public interface IExportModel {

    String type();
}
