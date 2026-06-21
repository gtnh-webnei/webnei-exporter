package moe.takochan.webnei.exporter.bundle;

import moe.takochan.webnei.exporter.domain.asset.render.AssetRenderProgress;

/** bundle 写出的运行期上下文。当前用于把资产渲染进度回调传递给 bundle writer。 */
public final class BundleContext {

    private static final BundleContext DEFAULT = new BundleContext(AssetRenderProgress.NONE);

    private final AssetRenderProgress renderProgress;

    private BundleContext(AssetRenderProgress renderProgress) {
        this.renderProgress = renderProgress;
    }

    public static BundleContext defaults() {
        return DEFAULT;
    }

    public static BundleContext withRenderProgress(AssetRenderProgress renderProgress) {
        return new BundleContext(renderProgress == null ? AssetRenderProgress.NONE : renderProgress);
    }

    public AssetRenderProgress getRenderProgress() {
        return renderProgress;
    }
}
