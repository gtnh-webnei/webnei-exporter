package moe.takochan.webnei.exporter.domain.item;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.item.model.ItemListEntryRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemToolClassRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;

/** item 数据域中间模型，包含 item、item_variant、item_tool_class、item_list_entry 四类行。 */
@Getter
public final class ItemExportModel implements IExportModel {

    /** 模型类型标识，供 bundle writer 选择具体映射逻辑。 */
    public static final String TYPE = "item";

    /** registry item 维度的基础行。 */
    private final List<ItemRow> items;

    /** ItemStack variant 维度的展示和身份行。 */
    private final List<ItemVariantRow> variants;

    /** 每个 item variant 的工具类型补充行。 */
    private final List<ItemToolClassRow> toolClasses;

    /** 物品列表展示入口行。 */
    private final List<ItemListEntryRow> listEntries;

    public ItemExportModel(List<ItemRow> items, List<ItemVariantRow> variants, List<ItemToolClassRow> toolClasses,
        List<ItemListEntryRow> listEntries) {
        this.items = Collections.unmodifiableList(items);
        this.variants = Collections.unmodifiableList(variants);
        this.toolClasses = Collections.unmodifiableList(toolClasses);
        this.listEntries = Collections.unmodifiableList(listEntries);
    }

    @Override
    public String type() {
        return TYPE;
    }
}
