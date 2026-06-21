package moe.takochan.webnei.exporter.domain.asset.render;

/**
 * 图标渲染器。
 *
 * <p>
 * {@link #prepareTile} 在客户端线程廉价构造可批量渲染的 {@link IconTile}（静态图标）；返回 {@code null}
 * 表示该 job 不适合批量（如多帧动画），由调用方退回 {@link #renderImage} 逐个渲染。两者都只在客户端
 * 线程执行 GL 工作，不负责 PNG 编码或落盘。
 */
public interface IAssetRenderer {

    boolean supports(AssetRenderJob job);

    IconTile prepareTile(AssetRenderJob job) throws AssetRenderException;

    RenderedAsset renderImage(AssetRenderJob job) throws AssetRenderException;
}
