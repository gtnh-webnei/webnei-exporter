package moe.takochan.webnei.exporter.bundle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import moe.takochan.webnei.exporter.domain.ExportModelSet;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.asset.AssetExportModel;
import moe.takochan.webnei.exporter.domain.asset.model.AssetRow;
import moe.takochan.webnei.exporter.domain.asset.render.AssetRenderProgress;
import moe.takochan.webnei.exporter.domain.asset.render.AssetRenderService;

public final class AssetBundlePreparer {

    private final AssetRenderService assetRenderService;

    public AssetBundlePreparer() {
        this(new AssetRenderService());
    }

    public AssetBundlePreparer(AssetRenderService assetRenderService) {
        this.assetRenderService = assetRenderService;
    }

    public ExportModelSet prepare(ExportModelSet models, File outputDirectory) {
        return prepare(models, outputDirectory, AssetRenderProgress.NONE);
    }

    public ExportModelSet prepare(ExportModelSet models, File outputDirectory, AssetRenderProgress progress) {
        List<IExportModel> prepared = new ArrayList<>();
        for (IExportModel model : models.getModels()) {
            if (model instanceof AssetExportModel) {
                AssetExportModel assetModel = (AssetExportModel) model;
                if (!assetModel.getAssets()
                    .isEmpty() || assetModel.getRenderJobs()
                        .isEmpty()) {
                    prepared.add(model);
                    continue;
                }
                List<AssetRow> rows = assetRenderService
                    .renderAll(assetModel.getRenderJobs(), outputDirectory, progress);
                prepared.add(assetModel.rendered(rows));
            } else {
                prepared.add(model);
            }
        }
        return new ExportModelSet(models.getPlanId(), prepared);
    }
}
