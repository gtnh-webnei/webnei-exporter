package moe.takochan.webnei.exporter.domain.nei.scan;

import lombok.Getter;

/** 运行时发现的一个唯一 NEI recipe handler 描述。 */
@Getter
public final class NeiHandlerDescriptor {

    /** handler 在 NEI 注册列表扫描中的顺序。 */
    private final int registrationIndex;

    /** handler 来源列表，例如 crafting、usage、serial_crafting。 */
    private final String sourceList;

    /** 由 handler class、handler id、overlay id 组成的稳定 key。 */
    private final String stableKey;

    /** handler 实例的完整 Java class 名。 */
    private final String handlerClass;

    /** handler.getHandlerId() 返回值。 */
    private final String handlerId;

    /** handler.getOverlayIdentifier() 返回值。 */
    private final String overlayId;

    /** handler.getRecipeName() 返回值。 */
    private final String recipeName;

    /** handler.getRecipeTabName() 返回值。 */
    private final String recipeTabName;

    /** exporter 解析出的最终 recipe category ID。 */
    private final String resolvedCategoryId;

    /** handler 归属 mod id，无法解析时为空。 */
    private final String modId;

    /** handler 归属 mod 显示名称，无法解析时为空。 */
    private final String modName;

    /** HandlerInfo icon ItemStack 的简短文本身份。 */
    private final String iconStackId;

    /** RecipeCatalysts 中该 handler 对应的 key。 */
    private final String catalystKey;

    /** 扫描阶段已知的 recipe 数量；未加载时为 -1。 */
    private final int loadedRecipeCount;

    /** 扫描阶段的抽取状态说明。 */
    private final String extractionStatus;

    /** 状态补充说明或错误原因。 */
    private final String reason;

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
