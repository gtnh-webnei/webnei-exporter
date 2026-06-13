package moe.takochan.webnei.exporter.nei.recipe;

public final class ExtractedStack {

    public final String handlerKey;
    public final int recipeIndex;
    public final String stackSource;
    public final int stackIndex;
    public final int x;
    public final int y;
    public final int candidateCount;
    public final String firstCandidate;
    public final String status;
    public final String reason;

    ExtractedStack(String handlerKey, int recipeIndex, String stackSource, int stackIndex, int x, int y,
        int candidateCount, String firstCandidate, String status, String reason) {
        this.handlerKey = handlerKey;
        this.recipeIndex = recipeIndex;
        this.stackSource = stackSource;
        this.stackIndex = stackIndex;
        this.x = x;
        this.y = y;
        this.candidateCount = candidateCount;
        this.firstCandidate = firstCandidate;
        this.status = status;
        this.reason = reason;
    }
}
