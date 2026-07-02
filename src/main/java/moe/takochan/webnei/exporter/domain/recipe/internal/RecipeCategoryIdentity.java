package moe.takochan.webnei.exporter.domain.recipe.internal;

import net.minecraft.item.ItemStack;

import codechicken.nei.drawable.DrawableResource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 单个 NEI recipe handler 解析出的分类身份与运行时素材，是 recipe category 去重和建行的依据。 */
@Getter
@RequiredArgsConstructor
public final class RecipeCategoryIdentity {

    /** handler class + handlerId + overlayId 组成的稳定去重与匹配 key。 */
    private final String handlerKey;

    /**
     * 确定性的分类 ID。
     *
     * <p>
     * 直接由 handler 身份（class + handlerId + overlayId）派生，不依赖运行时 modId，也不依赖本次扫描里其它 handler 是否撞名， 因此同一个 handler
     * 在任何环境、任何扫描组合下都得到恒定的 ID。
     */
    private final String categoryId;

    /** 分类显示名。 */
    private final String displayName;

    /** 分类归属 mod id。 */
    private final String modId;

    /** 分类画布宽度，前端绘制 handler 整页背景使用。 */
    private final int canvasWidth;

    /** 分类画布高度，前端绘制 handler 整页背景使用。 */
    private final int canvasHeight;

    /** NEI handler 的 y 偏移；不写表，仅供 collector 把 slot rely 折算成统一坐标系。 */
    private final int yShift;

    /** 分类图标的原始 ItemStack，仅供 asset domain 渲染，可能为空。 */
    private final ItemStack iconStack;

    /**
     * 分类图标的 NEI 自绘贴图，仅供 asset domain 渲染，可能为空。
     *
     * <p>
     * 来自 {@link codechicken.nei.recipe.HandlerInfo#getImage()}。与 iconStack 互斥：handler 要么用展示物品、要么用贴图。
     */
    private final DrawableResource iconImage;
}
