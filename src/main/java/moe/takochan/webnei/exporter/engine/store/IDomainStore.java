package moe.takochan.webnei.exporter.engine.store;

/**
 * domain store 标准接口。
 *
 * <p>
 * Store 作为跨 domain public boundary，统一暴露结果集对象和注册职责对象。
 */
public interface IDomainStore<D extends IDomainData, R extends IDomainRegistrar> {

    /** 返回当前 domain 的结果集对象。 */
    D data();

    /** 返回当前 domain 的注册职责对象。 */
    R registrar();
}
