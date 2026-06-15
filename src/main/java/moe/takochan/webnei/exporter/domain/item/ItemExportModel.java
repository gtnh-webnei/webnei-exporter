package moe.takochan.webnei.exporter.domain.item;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.item.model.ItemRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemToolClassRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.domain.item.model.NeiItemPanelEntryRow;

/** item 数据域中间模型，包含 item、item_variant、item_tool_class、nei_item_panel_entry 四类行。 */
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

    /** NEI item panel 当前展示顺序行。 */
    private final List<NeiItemPanelEntryRow> panelEntries;

    public ItemExportModel(List<ItemRow> items, List<ItemVariantRow> variants, List<ItemToolClassRow> toolClasses,
        List<NeiItemPanelEntryRow> panelEntries) {
        this.items = Collections.unmodifiableList(items);
        this.variants = Collections.unmodifiableList(variants);
        this.toolClasses = Collections.unmodifiableList(toolClasses);
        this.panelEntries = Collections.unmodifiableList(panelEntries);
    }

    @Override
    public String type() {
        return TYPE;
    }
}
