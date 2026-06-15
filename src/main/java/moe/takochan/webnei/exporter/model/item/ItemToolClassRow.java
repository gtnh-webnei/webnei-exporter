package moe.takochan.webnei.exporter.model.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** item_tool_class 表行：具体 item_variant 的工具类型补充信息。 */
@Getter
@RequiredArgsConstructor
public final class ItemToolClassRow {

    /** 所属 dataset ID。 */
    private final String datasetId;

    /** 所属 item_variant ID。 */
    private final String itemVariantId;

    /** Minecraft/Forge 工具类型名称，例如 pickaxe、shovel。 */
    private final String toolClass;

    /** 对应工具类型的 harvest level。 */
    private final int harvestLevel;
}
