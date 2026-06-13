package moe.takochan.webnei.exporter.bundle;

public final class BundleContext {

    private static final BundleContext DEFAULT = new BundleContext();

    private BundleContext() {}

    public static BundleContext defaults() {
        return DEFAULT;
    }
}
