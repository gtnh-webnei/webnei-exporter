package moe.takochan.webnei.exporter.domain.asset.render;

/**
 * 图标渲染器。
 *
 * <p>
 * {@link #renderImage} 只在客户端线程执行 GL 渲染并返回内存产物，不负责 PNG 编码或落盘。
 * 调度（批量、按帧时间片）由 {@link AssetRenderService} 统一驱动。
 */
public interface IAssetRenderer {

    boolean supports(AssetRenderJob job);

    RenderedAsset renderImage(AssetRenderJob job) throws AssetRenderException;
}
