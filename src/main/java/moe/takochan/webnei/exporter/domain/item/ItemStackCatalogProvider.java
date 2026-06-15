package moe.takochan.webnei.exporter.domain.item;

import moe.takochan.webnei.exporter.adapter.AdapterContext;
import moe.takochan.webnei.exporter.adapter.AdapterRegistry;
import moe.takochan.webnei.exporter.domain.asset.AssetRequestRegistryProvider;
import moe.takochan.webnei.exporter.domain.dataset.task.DatasetIdentity;
import moe.takochan.webnei.exporter.engine.task.ExportTaskContext;

/**
 * 从 {@link ExportTaskContext} 获取同一次导出共享的 ItemStackCatalog。
 *
 * <p>
 * 后续 recipe、quest、ore dictionary、asset 等 step 需要补充 ItemStack 时，应复用同一个 catalog，而不是各自创建
 * 私有 catalog。
 */
public final class ItemStackCatalogProvider {

    private static final String KEY = "item.stack.catalog";

    private ItemStackCatalogProvider() {}

    public static ItemStackCatalog getOrCreate(ExportTaskContext context, DatasetIdentity dataset) {
        Object value = context.get(KEY);
        if (value instanceof ItemStackCatalog catalog) {
            return catalog;
        }
        ItemStackCatalog catalog = new ItemStackCatalog(
            dataset,
            new ForgeItemIdentityResolver(),
            new ItemStackDetailCollector(),
            new ItemToolClassCollector(),
            AssetRequestRegistryProvider.getOrCreate(context),
            AdapterRegistry.defaults(),
            new AdapterContext());
        context.put(KEY, catalog);
        return catalog;
    }
}
