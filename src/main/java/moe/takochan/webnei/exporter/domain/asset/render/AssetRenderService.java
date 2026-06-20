package moe.takochan.webnei.exporter.domain.asset.render;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import moe.takochan.webnei.exporter.WebneiExporterMod;
import moe.takochan.webnei.exporter.domain.asset.model.AssetRow;

public final class AssetRenderService {

    private final List<IAssetRenderer> renderers;

    public AssetRenderService() {
        this(Arrays.<IAssetRenderer>asList(new ItemIconRenderer(), new FluidIconRenderer()));
    }

    public AssetRenderService(List<IAssetRenderer> renderers) {
        this.renderers = renderers;
    }

    public List<AssetRow> renderAll(List<AssetRenderJob> jobs, File outputDirectory) {
        List<AssetRow> rows = new ArrayList<>();
        for (AssetRenderJob job : jobs) {
            IAssetRenderer renderer = rendererFor(job);
            if (renderer == null) {
                WebneiExporterMod.LOG.warn(
                    "No asset renderer for ownerType={}, ownerId={}, kind={}",
                    job.getOwnerType(),
                    job.getOwnerId(),
                    job.getKind());
                continue;
            }
            try {
                rows.add(toRow(job, renderer.render(job, outputDirectory)));
            } catch (AssetRenderException | RuntimeException e) {
                WebneiExporterMod.LOG.warn(
                    "Failed to render asset: ownerType={}, ownerId={}, kind={}",
                    job.getOwnerType(),
                    job.getOwnerId(),
                    job.getKind(),
                    e);
            }
        }
        return rows;
    }

    private IAssetRenderer rendererFor(AssetRenderJob job) {
        for (IAssetRenderer renderer : renderers) {
            if (renderer.supports(job)) {
                return renderer;
            }
        }
        return null;
    }

    private static AssetRow toRow(AssetRenderJob job, AssetRenderResult result) {
        return new AssetRow(
            job.getDatasetId(),
            job.getOwnerType(),
            job.getOwnerId(),
            job.getKind(),
            result.getPath(),
            result.getMediaType(),
            result.getSha256(),
            result.getWidth(),
            result.getHeight(),
            result.getMetadataJson());
    }
}
