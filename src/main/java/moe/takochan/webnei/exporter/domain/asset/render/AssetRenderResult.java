package moe.takochan.webnei.exporter.domain.asset.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class AssetRenderResult {

    private static final String MEDIA_TYPE_PNG = "image/png";

    private final String path;
    private final String mediaType;
    private final int width;
    private final int height;
    private final String metadataJson;

    public static AssetRenderResult png(String path, int width, int height, String metadataJson) {
        return new AssetRenderResult(path, MEDIA_TYPE_PNG, width, height, metadataJson);
    }
}
