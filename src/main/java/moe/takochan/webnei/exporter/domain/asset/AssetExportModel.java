package moe.takochan.webnei.exporter.domain.asset;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.asset.model.AssetRow;

/** asset 数据域中间模型。 */
@Getter
public final class AssetExportModel implements IExportModel {

    /** 模型类型标识，供 bundle writer 选择具体映射逻辑。 */
    public static final String TYPE = "asset";

    /** asset 表行。 */
    private final List<AssetRow> assets;

    public AssetExportModel(List<AssetRow> assets) {
        this.assets = Collections.unmodifiableList(assets);
    }

    @Override
    public String type() {
        return TYPE;
    }
}
