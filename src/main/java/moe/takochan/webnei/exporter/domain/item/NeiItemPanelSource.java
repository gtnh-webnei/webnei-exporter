package moe.takochan.webnei.exporter.domain.item;

import java.util.List;

import net.minecraft.item.ItemStack;

import codechicken.nei.ItemList;

/**
 * 读取 NEI 当前 item panel 列表。
 *
 * <p>
 * {@code ItemList.items} 是展示源，不是全量 ItemStack 权威。这里用它作为当前 item 阶段的初始种子：每个 stack 先进入
 * ItemStackCatalog.register，再额外记录 panel_index。
 */
public final class NeiItemPanelSource implements IItemPanelSource {

    @Override
    public void collect(ItemStackCatalog catalog) {
        List<ItemStack> items = ItemList.items;
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack != null && stack.getItem() != null) {
                String itemVariantId = catalog.register(stack);
                catalog.addPanelEntry(itemVariantId, i);
            }
        }
    }
}
