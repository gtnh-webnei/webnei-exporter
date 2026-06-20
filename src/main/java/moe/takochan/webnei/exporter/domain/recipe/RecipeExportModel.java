package moe.takochan.webnei.exporter.domain.recipe;

import moe.takochan.webnei.exporter.domain.IExportModel;

/** recipe 数据域中间模型。 */
public final class RecipeExportModel implements IExportModel {

    /** 模型类型标识，供 bundle writer 选择具体映射逻辑。 */
    public static final String TYPE = "recipe";

    @Override
    public String type() {
        return TYPE;
    }
}
