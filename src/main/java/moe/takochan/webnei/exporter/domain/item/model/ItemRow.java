package moe.takochan.webnei.exporter.domain.item.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** item 表行：从 ItemStack 归并出的 registry item 基础字段。 */
@Getter
@RequiredArgsConstructor
public final class ItemRow {

    /** 所属 dataset ID。 */
    private final String datasetId;

    /** registry item 稳定 ID，通常为 modid:name。 */
    private final String itemId;

    /** item 所属 mod id。 */
    private final String modId;

    /** Forge registry name 的本地名称部分。 */
    private final String registryName;

    /** ItemStack 暴露的未本地化名称。 */
    private final String unlocalizedName;

    /** 当前 ItemStack API 返回的最大堆叠数量。 */
    private final int maxStackSize;

    /** item 最大耐久值。 */
    private final int maxDamage;

    /** 当前运行时 Item registry 数字 ID，仅用于诊断，不作为稳定身份。 */
    private final int runtimeItemId;
}
