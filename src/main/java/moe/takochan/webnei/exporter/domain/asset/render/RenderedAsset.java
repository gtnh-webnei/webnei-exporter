package moe.takochan.webnei.exporter.domain.asset.render;

import java.awt.image.BufferedImage;

/**
 * 一个图标的内存渲染产物。
 *
 * <p>
 * GL 渲染阶段（客户端线程）只产出该对象，不做 PNG 编码或落盘。编码（PNG 字节）与写出（追加进 zip）
 * 由后台 encoder 线程池消费该对象时完成；{@link #relativePath} 即 bundle 内（zip 内）相对路径。
 */
public final class RenderedAsset {

    public static final String MEDIA_TYPE_PNG = "image/png";

    private final AssetRenderJob job;
    private final String relativePath;
    private final BufferedImage image;
    private final String mediaType;
    private final String metadataJson;

    private RenderedAsset(AssetRenderJob job, String relativePath, BufferedImage image, String mediaType,
        String metadataJson) {
        this.job = job;
        this.relativePath = relativePath;
        this.image = image;
        this.mediaType = mediaType;
        this.metadataJson = metadataJson;
    }

    public static RenderedAsset png(AssetRenderJob job, String relativePath, BufferedImage image, String metadataJson) {
        return new RenderedAsset(job, relativePath, image, MEDIA_TYPE_PNG, metadataJson);
    }

    public AssetRenderJob getJob() {
        return job;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getMetadataJson() {
        return metadataJson;
    }
}
