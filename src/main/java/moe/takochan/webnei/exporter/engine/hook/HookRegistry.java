package moe.takochan.webnei.exporter.engine.hook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import moe.takochan.webnei.exporter.WebneiExporterMod;

/**
 * hook 注册表。在 mod postInit 阶段扫描并初始化，之后提供按类型获取 hook 的能力。
 */
public final class HookRegistry {

    private static HookRegistry instance;

    private final Map<Class<? extends IExportHook>, List<IExportHook>> hooksByType;

    private HookRegistry(List<IExportHook> hooks) {
        Map<Class<? extends IExportHook>, List<IExportHook>> index = new LinkedHashMap<>();
        for (IExportHook hook : hooks) {
            for (Class<? extends IExportHook> hookType : hookTypes(hook.getClass())) {
                index.computeIfAbsent(hookType, ignored -> new ArrayList<>())
                    .add(hook);
            }
        }
        Map<Class<? extends IExportHook>, List<IExportHook>> immutable = new LinkedHashMap<>();
        for (Map.Entry<Class<? extends IExportHook>, List<IExportHook>> entry : index.entrySet()) {
            immutable.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }
        this.hooksByType = Collections.unmodifiableMap(immutable);
    }

    /**
     * 在 FMLPostInitializationEvent 阶段调用，加载并初始化所有可用 hook。
     */
    public static void init() {
        List<IExportHook> hooks = new ArrayList<>();
        for (IExportHook hook : ServiceLoader.load(IExportHook.class)) {
            if (hook.isAvailable()) {
                hooks.add(hook);
            } else {
                WebneiExporterMod.LOG.info(
                    "Hook unavailable: {}",
                    hook.getClass()
                        .getName());
            }
        }
        instance = new HookRegistry(hooks);
        WebneiExporterMod.LOG.info("HookRegistry initialized, registered {} hooks", hooks.size());
    }

    /**
     * 获取指定 hook 接口的所有可用实现。
     */
    public static <T extends IExportHook> List<T> get(Class<T> hookType) {
        if (instance == null) {
            throw new IllegalStateException("HookRegistry not initialized. Call HookRegistry.init() in postInit.");
        }
        List<IExportHook> hooks = instance.hooksByType.get(hookType);
        if (hooks == null) {
            return Collections.emptyList();
        }
        List<T> out = new ArrayList<>();
        for (IExportHook hook : hooks) {
            out.add(hookType.cast(hook));
        }
        return Collections.unmodifiableList(out);
    }

    /**
     * 收集 hook 实现类声明的所有 hook 接口类型。
     */
    @SuppressWarnings("unchecked")
    private static Set<Class<? extends IExportHook>> hookTypes(Class<?> hookClass) {
        Set<Class<? extends IExportHook>> types = new LinkedHashSet<>();
        collectHookTypes(hookClass, types);
        types.add(IExportHook.class);
        return types;
    }

    /**
     * 递归收集 hook 接口类型。
     */
    @SuppressWarnings("unchecked")
    private static void collectHookTypes(Class<?> type, Set<Class<? extends IExportHook>> out) {
        if (type == null || type == Object.class) {
            return;
        }
        for (Class<?> iface : type.getInterfaces()) {
            if (IExportHook.class.isAssignableFrom(iface)) {
                out.add((Class<? extends IExportHook>) iface);
            }
            collectHookTypes(iface, out);
        }
        collectHookTypes(type.getSuperclass(), out);
    }
}
