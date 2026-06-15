package moe.takochan.webnei.exporter.engine.store;

import java.util.List;

import moe.takochan.webnei.exporter.domain.IExportModel;

/**
 * domain 数据共享的标准接口。
 *
 * <p>
 * 每个 domain 实现此接口，对外提供受控的读写能力。
 * 写入操作（{@link #add}）由 domain 自己定义内部处理逻辑，调用方不感知实现细节。
 *
 * @param <I> 输入类型，其他 domain 传入的原始数据
 * @param <M> 领域模型类型，domain 产出的数据对象
 */
public interface IDomainStore<I, M> {

    /** 写入一条数据，domain 内部执行处理逻辑并返回领域模型对象。 */
    M add(I input);

    /** 按 key 查询已有模型。 */
    M get(String key);

    /** 获取当前全量数据。 */
    List<M> list();

    /** 将当前数据集转为最终导出模型，由引擎统一收集。 */
    IExportModel toExportModel();
}
