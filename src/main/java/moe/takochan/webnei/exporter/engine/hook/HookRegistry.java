package moe.takochan.webnei.exporter.engine.hook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import moe.takochan.webnei.exporter.WebneiExporterMod;

/**
 * hook 注册表。在 mod postInit 阶段扫描并初始化，之后提供按类型获取 hook 的能力。
 */
public final class HookRegistry {

    private static HookRegistry instance;

    private final List<IExportHook> hooks;

    private HookRegistry(List<IExportHook> hooks) {
        this.hooks = Collections.unmodifiableList(hooks);
    }

    /**
     * 在 FMLPostInitializationEvent 阶段调用，扫描并实例化所有可用 hook。
     */
    public static void init() {
        List<IExportHook> hooks = new ArrayList<>();
        for (Class<?> clazz : HookProviderDiscovery.scanAll()) {
            if (IExportHook.class.isAssignableFrom(clazz) && !clazz.isInterface() && !clazz.isEnum()) {
                try {
                    IExportHook hook = (IExportHook) clazz.getDeclaredConstructor()
                        .newInstance();
                    if (hook.isAvailable()) {
                        hooks.add(hook);
                    }
                } catch (Exception e) {
                    WebneiExporterMod.LOG.warn("Failed to instantiate hook: {}", clazz.getName(), e);
                }
            }
        }
        instance = new HookRegistry(hooks);
        WebneiExporterMod.LOG.info("HookRegistry initialized, registered {} hooks", hooks.size());
    }

    /**
     * 获取指定 hook 接口的所有可用实现。
     *
     * @param hookType hook 接口类型
     * @param <T>      hook 接口类型
     * @return 已过滤 isAvailable() 的 hook 实例列表
     */
    public static <T extends IExportHook> List<T> get(Class<T> hookType) {
        if (instance == null) {
            throw new IllegalStateException("HookRegistry not initialized. Call HookRegistry.init() in postInit.");
        }
        List<T> out = new ArrayList<>();
        for (IExportHook hook : instance.hooks) {
            if (hookType.isInstance(hook)) {
                out.add(hookType.cast(hook));
            }
        }
        return Collections.unmodifiableList(out);
    }
}
