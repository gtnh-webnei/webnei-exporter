package moe.takochan.webnei.exporter.domain.item.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/** item_variant 表行：具体 ItemStack 身份和展示详情。 */
@Getter
@RequiredArgsConstructor
public final class ItemVariantRow {

    /** 所属 dataset ID。 */
    private final String datasetId;

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

    /** 当前语言环境下的显示名称，保留 Minecraft 格式码。 */
    private final String displayName;

    /** 由 item variant hook 补充的化学式或材料表达式；未知时为空。 */
    @Setter
    private String chemicalExpression = "";
}
