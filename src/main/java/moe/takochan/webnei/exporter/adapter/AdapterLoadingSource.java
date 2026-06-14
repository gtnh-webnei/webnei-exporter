package moe.takochan.webnei.exporter.adapter;

public enum AdapterLoadingSource {

    NONE(""),
    HANDLER_RECIPE_ID("handler:getRecipeID"),
    HANDLER_API("handler-api");

    public final String label;

    AdapterLoadingSource(String label) {
        this.label = label;
    }
}
