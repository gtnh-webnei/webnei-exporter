package moe.takochan.webnei.exporter.engine.store;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import moe.takochan.webnei.exporter.domain.IExportModel;

/**
 * domain store 注册表，持有本次导出所有 store 实例。
 *
 * <p>
 * task 执行完成后注册自己的 store，后续 task 按具体类型获取。
 */
public final class DomainStoreRegistry {

    private final Map<Class<?>, IDomainStore<?, ?>> stores = new LinkedHashMap<>();

    /** 注册一个 store 实例。 */
    public void register(Class<? extends IDomainStore<?, ?>> type, IDomainStore<?, ?> store) {
        stores.put(type, store);
    }

    /** 按类型获取 store。 */
    @SuppressWarnings("unchecked")
    public <T extends IDomainStore<?, ?>> T get(Class<T> type) {
        T store = (T) stores.get(type);
        if (store == null) {
            throw new IllegalStateException("Domain store not registered: " + type.getName());
        }
        return store;
    }

    /** 收集所有 store 的导出模型。 */
    public List<IExportModel> collectModels() {
        List<IExportModel> models = new ArrayList<>();
        for (IDomainStore<?, ?> store : stores.values()) {
            IExportModel model = store.data()
                .toExportModel();
            if (model != null) {
                models.add(model);
            }
        }
        return models;
    }
}
