package moe.takochan.webnei.exporter.model.nei;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moe.takochan.webnei.exporter.model.IExportModel;
import moe.takochan.webnei.exporter.nei.recipe.SlotExtraction;

/** NEI final-result recipe visual facts 验证模型。 */
@Getter
@RequiredArgsConstructor
public final class RecipeVisualFactsExportModel implements IExportModel {

    /** 模型类型标识，供 bundle writer 选择具体映射逻辑。 */
    public static final String TYPE = "recipe-visual-facts";

    /** handler、recipe、slot、candidate 四类抽取结果集合。 */
    private final SlotExtraction extraction;

    @Override
    public String type() {
        return TYPE;
    }
}
