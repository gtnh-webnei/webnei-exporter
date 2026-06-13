package moe.takochan.webnei.exporter.nei.scan;

/** Domain description of one unique NEI recipe handler discovered at runtime. */
public final class NeiHandlerDescriptor {

    public final int registrationIndex;
    public final String sourceList;
    public final String stableKey;
    public final String handlerClass;
    public final String handlerId;
    public final String overlayId;
    public final String recipeName;
    public final String recipeTabName;
    public final String resolvedCategoryId;
    public final String modId;
    public final String modName;
    public final String iconStackId;
    public final String catalystKey;
    public final int loadedRecipeCount;
    public final String extractionStatus;
    public final String reason;

    public NeiHandlerDescriptor(int registrationIndex, String sourceList, String stableKey, String handlerClass,
        String handlerId, String overlayId, String recipeName, String recipeTabName, String resolvedCategoryId,
        String modId, String modName, String iconStackId, String catalystKey, int loadedRecipeCount,
        String extractionStatus, String reason) {
        this.registrationIndex = registrationIndex;
        this.sourceList = sourceList;
        this.stableKey = stableKey;
        this.handlerClass = handlerClass;
        this.handlerId = handlerId;
        this.overlayId = overlayId;
        this.recipeName = recipeName;
        this.recipeTabName = recipeTabName;
        this.resolvedCategoryId = resolvedCategoryId;
        this.modId = modId;
        this.modName = modName;
        this.iconStackId = iconStackId;
        this.catalystKey = catalystKey;
        this.loadedRecipeCount = loadedRecipeCount;
        this.extractionStatus = extractionStatus;
        this.reason = reason;
    }
}
