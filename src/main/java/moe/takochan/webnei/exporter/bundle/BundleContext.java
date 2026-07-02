package moe.takochan.webnei.exporter.bundle;

import moe.takochan.webnei.exporter.domain.asset.render.AssetRenderProgress;

/** bundle 写出的运行期上下文。承载资产渲染进度回调与 asset 渲染选项 flag。 */
public final class BundleContext {

    private static final BundleContext DEFAULT = new BundleContext(AssetRenderProgress.NONE, false, false);

    private final AssetRenderProgress renderProgress;
    private final boolean skipAssetRender;
    private final boolean skipAssetAnimations;

    private BundleContext(AssetRenderProgress renderProgress, boolean skipAssetRender, boolean skipAssetAnimations) {
        this.renderProgress = renderProgress;
        this.skipAssetRender = skipAssetRender;
        this.skipAssetAnimations = skipAssetAnimations;
    }

    public static BundleContext defaults() {
        return DEFAULT;
    }

    public static BundleContext withRenderProgress(AssetRenderProgress renderProgress) {
        return new BundleContext(renderProgress == null ? AssetRenderProgress.NONE : renderProgress, false, false);
    }

    /** 以现有 renderProgress 为基础，附加 asset 渲染选项 flag。 */
    public static BundleContext create(AssetRenderProgress renderProgress, boolean skipAssetRender,
        boolean skipAssetAnimations) {
        return new BundleContext(
            renderProgress == null ? AssetRenderProgress.NONE : renderProgress,
            skipAssetRender,
            skipAssetAnimations);
    }

    public AssetRenderProgress getRenderProgress() {
        return renderProgress;
    }

    /** true 表示本次不导出任何图片资源。 */
    public boolean isSkipAssetRender() {
        return skipAssetRender;
    }

    /** true 表示动图序列退化为静态首帧。 */
    public boolean isSkipAssetAnimations() {
        return skipAssetAnimations;
    }
}
