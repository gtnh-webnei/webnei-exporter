package moe.takochan.webnei.exporter.bundle.record;

import java.util.List;

import moe.takochan.webnei.exporter.domain.IExportModel;

/** 将一个 export model 映射成一个或多个 bundle record set。 */
public interface IBundleRecordSetMapper<T extends IExportModel> {

    /** mapper 支持的 export model 类型。 */
    Class<T> modelType();

    /** 将 model 转换为 bundle record set 列表。 */
    List<BundleRecordSet> recordSets(T model);

    /** 转换已知类型的 export model。 */
    default List<BundleRecordSet> recordSetsFor(IExportModel model) {
        return recordSets(modelType().cast(model));
    }
}
