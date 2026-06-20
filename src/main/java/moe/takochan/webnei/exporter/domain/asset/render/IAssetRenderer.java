package moe.takochan.webnei.exporter.domain.asset.render;

import java.io.File;

public interface IAssetRenderer {

    boolean supports(AssetRenderJob job);

    AssetRenderResult render(AssetRenderJob job, File outputDirectory) throws AssetRenderException;
}
