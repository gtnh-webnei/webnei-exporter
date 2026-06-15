package moe.takochan.webnei.exporter.domain.asset;

import moe.takochan.webnei.exporter.engine.task.ExportTaskContext;

/** 从 ExportTaskContext 获取同一次导出共享的 AssetRequestRegistry。 */
public final class AssetRequestRegistryProvider {

    private static final String KEY = "asset.request.registry";

    private AssetRequestRegistryProvider() {}

    public static AssetRequestRegistry getOrCreate(ExportTaskContext context) {
        Object value = context.get(KEY);
        if (value instanceof AssetRequestRegistry registry) {
            return registry;
        }
        AssetRequestRegistry registry = new AssetRequestRegistry(new AssetIdFactory());
        context.put(KEY, registry);
        return registry;
    }
}
