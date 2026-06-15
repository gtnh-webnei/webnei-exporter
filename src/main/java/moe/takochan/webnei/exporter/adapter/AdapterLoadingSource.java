package moe.takochan.webnei.exporter.adapter;

import lombok.Getter;

/** adapter 加载 NEI handler 时使用的来源类型。 */
@Getter
public enum AdapterLoadingSource {

    NONE(""),
    HANDLER_RECIPE_ID("handler:getRecipeID"),
    HANDLER_API("handler-api");

    /** 写入诊断结果的来源标签。 */
    private final String label;

    AdapterLoadingSource(String label) {
        this.label = label;
    }
}
