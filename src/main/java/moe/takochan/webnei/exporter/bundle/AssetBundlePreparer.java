package moe.takochan.webnei.exporter.bundle;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import moe.takochan.webnei.exporter.domain.ExportModelSet;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.asset.AssetExportModel;
import moe.takochan.webnei.exporter.domain.asset.model.AssetRow;
import moe.takochan.webnei.exporter.domain.asset.render.AssetRenderService;
import moe.takochan.webnei.exporter.domain.asset.render.FluidIconRenderer;
import moe.takochan.webnei.exporter.domain.asset.render.IAssetRenderer;
import moe.takochan.webnei.exporter.domain.asset.render.ItemIconRenderer;
import moe.takochan.webnei.exporter.domain.asset.render.RecipeCategoryAuxIconRenderer;

/**
 * 把 asset domain 的待渲染 job 落成静态/动图 PNG，并产出 asset 行集合。
 *
 * <p>
 * 渲染开关来自 {@link BundleContext}：
 * <ul>
 * <li>{@link BundleContext#isSkipAssetRender()}：整段跳过，AssetExportModel 输出空 asset rows，也不生成 assets.zip。</li>
 * <li>{@link BundleContext#isSkipAssetAnimations()}：动图退化为静态首帧，仍生成 PNG 与 assets.zip。</li>
 * </ul>
 */
public final class AssetBundlePreparer {

    public AssetBundlePreparer() {}

    public ExportModelSet prepare(ExportModelSet models, File outputDirectory, BundleContext context) {
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
                if (context.isSkipAssetRender()) {
                    prepared.add(assetModel.rendered(Collections.<AssetRow>emptyList()));
                    continue;
                }
                AssetRenderService service = buildService(context.isSkipAssetAnimations());
                List<AssetRow> rows = service
                    .renderAll(assetModel.getRenderJobs(), outputDirectory, context.getRenderProgress());
                prepared.add(assetModel.rendered(rows));
            } else {
                prepared.add(model);
            }
        }
        return new ExportModelSet(models.getPlanId(), prepared);
    }

    private static AssetRenderService buildService(boolean disableAnimations) {
        List<IAssetRenderer> renderers = Arrays.<IAssetRenderer>asList(
            new ItemIconRenderer(disableAnimations),
            new FluidIconRenderer(disableAnimations),
            new RecipeCategoryAuxIconRenderer());
        return new AssetRenderService(renderers);
    }
}
