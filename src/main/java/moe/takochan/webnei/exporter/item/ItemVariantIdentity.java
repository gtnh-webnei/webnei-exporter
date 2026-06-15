package moe.takochan.webnei.exporter.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 完整 ItemStack 维度的稳定身份：registry item id + raw damage + canonical NBT。 */
@Getter
@RequiredArgsConstructor
public final class ItemVariantIdentity {

    /** ItemStack variant 稳定 ID，由 itemId、raw damage 和 canonical NBT hash 组合。 */
    private final String itemVariantId;

    /** 所属 registry item 稳定 ID。 */
    private final String itemId;

    /** ItemStack 原始 damage/meta 值。 */
    private final int damage;

    /** canonical NBT 文本的短 hash；无 NBT 时为空。 */
    private final String nbtHash;

    /** canonical NBT 文本；无 NBT 时为空。 */
    private final String nbtText;
}
