package moe.takochan.webnei.exporter.nei.recipe;

public final class ExtractedCandidate {

    public final String handlerKey;
    public final int recipeIndex;
    public final String stackSource;
    public final int stackIndex;
    public final int candidateIndex;
    public final String itemId;
    public final int damage;
    public final int stackSize;
    public final String displayName;

    ExtractedCandidate(String handlerKey, int recipeIndex, String stackSource, int stackIndex, int candidateIndex,
        String itemId, int damage, int stackSize, String displayName) {
        this.handlerKey = handlerKey;
        this.recipeIndex = recipeIndex;
        this.stackSource = stackSource;
        this.stackIndex = stackIndex;
        this.candidateIndex = candidateIndex;
        this.itemId = itemId;
        this.damage = damage;
        this.stackSize = stackSize;
        this.displayName = displayName;
    }
}
