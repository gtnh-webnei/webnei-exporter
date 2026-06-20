package moe.takochan.webnei.exporter.domain.asset;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.asset.model.AssetRow;
import moe.takochan.webnei.exporter.domain.asset.render.AssetRenderJob;

/** asset 数据域中间模型。 */
@Getter
public final class AssetExportModel implements IExportModel {

    /** 模型类型标识，供 bundle writer 选择具体映射逻辑。 */
    public static final String TYPE = AssetContract.TYPE;

    /** 待渲染的 asset jobs。 */
    private final List<AssetRenderJob> renderJobs;

    /** 已渲染完成的 asset 表行。 */
    private final List<AssetRow> assets;

    private AssetExportModel(List<AssetRenderJob> renderJobs, List<AssetRow> assets) {
        this.renderJobs = Collections.unmodifiableList(renderJobs);
        this.assets = Collections.unmodifiableList(assets);
    }

    public static AssetExportModel pending(List<AssetRenderJob> renderJobs) {
        return new AssetExportModel(renderJobs, Collections.<AssetRow>emptyList());
    }

    public AssetExportModel rendered(List<AssetRow> assets) {
        return new AssetExportModel(renderJobs, assets);
    }

    @Override
    public String type() {
        return TYPE;
    }
}
