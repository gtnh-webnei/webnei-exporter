package moe.takochan.webnei.exporter.domain.recipe.internal;

import net.minecraft.item.ItemStack;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 单个 NEI recipe handler 解析出的分类身份与运行时素材，是 recipe category 去重和建行的依据。 */
@Getter
@RequiredArgsConstructor
public final class RecipeCategoryIdentity {

    /** handler class + handlerId + overlayId 组成的稳定去重 key。 */
    private final String handlerKey;

    /** 解析出的基础分类 ID，可能与其它 handler 撞名，由 data 做最终消歧。 */
    private final String baseCategoryId;

    /** 基础分类 ID 撞名时追加的消歧片段。 */
    private final String disambiguationSegment;

    /** 分类显示名。 */
    private final String displayName;

    /** 分类归属 mod id。 */
    private final String modId;

    /** 分类图标的原始 ItemStack，仅供 asset domain 渲染，可能为空。 */
    private final ItemStack iconStack;
}
