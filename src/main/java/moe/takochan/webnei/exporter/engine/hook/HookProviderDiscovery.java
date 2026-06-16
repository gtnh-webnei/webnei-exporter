package moe.takochan.webnei.exporter.engine.hook;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import moe.takochan.webnei.exporter.WebneiExporterMod;

/**
 * 扫描 mod 根包下所有 class，用于 hook 发现。
 */
final class HookProviderDiscovery {

    private HookProviderDiscovery() {}

    /**
     * 扫描 mod 根包下所有 class 并加载。
     *
     * @return 扫描到的所有 class
     */
    static List<Class<?>> scanAll() {
        String basePackage = WebneiExporterMod.class.getPackage().getName();
        try {
            return scanPackage(basePackage);
        } catch (Exception e) {
            WebneiExporterMod.LOG.error("Failed to scan classes for hook discovery", e);
            return Collections.emptyList();
        }
    }

    /**
     * 递归扫描包下所有 class 文件并加载。
     *
     * @param packageName 包名
     * @return 加载的 Class 列表
     */
    private static List<Class<?>> scanPackage(String packageName) throws Exception {
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);
        List<Class<?>> classes = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if ("file".equals(resource.getProtocol())) {
                scanDirectory(new File(resource.toURI()), packageName, classes);
            }
        }
        return classes;
    }

    /**
     * 递归扫描目录下 .class 文件。
     *
     * @param directory   目录
     * @param packageName 当前包名
     * @param classes     class 输出列表
     */
    private static void scanDirectory(File directory, String packageName, List<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                    // mod 依赖不在时跳过
                }
            }
        }
    }
}
