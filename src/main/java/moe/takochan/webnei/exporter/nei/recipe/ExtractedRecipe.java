package moe.takochan.webnei.exporter.nei.recipe;

public final class ExtractedRecipe {

    public final String handlerKey;
    public final int recipeIndex;
    public final String fingerprint;
    public final int ingredientCount;
    public final int resultCount;
    public final int otherCount;
    public final String status;
    public final String reason;

    ExtractedRecipe(String handlerKey, int recipeIndex, String fingerprint, int ingredientCount, int resultCount,
        int otherCount, String status, String reason) {
        this.handlerKey = handlerKey;
        this.recipeIndex = recipeIndex;
        this.fingerprint = fingerprint;
        this.ingredientCount = ingredientCount;
        this.resultCount = resultCount;
        this.otherCount = otherCount;
        this.status = status;
        this.reason = reason;
    }
}
