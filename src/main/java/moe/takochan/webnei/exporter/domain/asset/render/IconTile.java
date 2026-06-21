package moe.takochan.webnei.exporter.domain.asset.render;

import moe.takochan.webnei.exporter.domain.asset.render.client.FboIconRenderer;

/**
 * 一个可批量渲染的图标单元。
 *
 * <p>
 * 在客户端线程廉价构造：仅确定画布尺寸、延迟的 GL 绘制动作以及目标资产元信息，不触发实际渲染。
 * 实际 GL 渲染由 {@link AssetRenderService} 收集同尺寸的一批 tile 后统一交给 atlas 渲染器执行。
 */
public final class IconTile {

    private final AssetRenderJob job;
    private final String relativePath;
    private final int size;
    private final FboIconRenderer.IconRenderAction action;
    private final String metadataJson;

    public IconTile(AssetRenderJob job, String relativePath, int size, FboIconRenderer.IconRenderAction action,
        String metadataJson) {
        this.job = job;
        this.relativePath = relativePath;
        this.size = size;
        this.action = action;
        this.metadataJson = metadataJson;
    }

    public AssetRenderJob getJob() {
        return job;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public int getSize() {
        return size;
    }

    public FboIconRenderer.IconRenderAction getAction() {
        return action;
    }

    public String getMetadataJson() {
        return metadataJson;
    }
}
