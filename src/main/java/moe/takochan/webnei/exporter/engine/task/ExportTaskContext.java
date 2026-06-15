package moe.takochan.webnei.exporter.engine.task;

import moe.takochan.webnei.exporter.engine.ExportExecutionContext;
import moe.takochan.webnei.exporter.engine.store.DomainStoreRegistry;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/**
 * task 执行时的上下文。
 *
 * <p>
 * task 通过 {@link #store(Class)} 获取前置 domain 已注册的 store，
 * 通过 {@link #register(Class, IDomainStore)} 在自己执行完成后注册本 domain 的 store。
 */
public final class ExportTaskContext {

    private final ExportExecutionContext executionContext;
    private final DomainStoreRegistry storeRegistry;

    public ExportTaskContext(ExportExecutionContext executionContext, DomainStoreRegistry storeRegistry) {
        this.executionContext = executionContext;
        this.storeRegistry = storeRegistry;
    }

    /** job/session 级上下文，供 task 读取当前导出状态。 */
    public ExportExecutionContext executionContext() {
        return executionContext;
    }

    /** 获取前置 domain 已注册的 store。 */
    public <T extends IDomainStore<?, ?>> T store(Class<T> type) {
        return storeRegistry.get(type);
    }

    /** task 执行完成后注册自己的 domain store，供后续 task 使用。 */
    public <I, M> void register(Class<? extends IDomainStore<I, M>> type, IDomainStore<I, M> store) {
        storeRegistry.register(type, store);
    }
}
