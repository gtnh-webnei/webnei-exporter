package moe.takochan.webnei.exporter.domain.item.internal;

import java.util.List;

import net.minecraft.item.ItemStack;

import codechicken.nei.ItemList;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;

/**
 * 读取 NEI 当前 item panel 列表。
 *
 * <p>
 * {@code ItemList.items} 是展示源，不是全量 ItemStack 权威。这里用它作为 item 列表的初始种子：
 * 每个 stack 进入 store.add，再记录展示顺序。
 */
public final class NeiItemPanelSource {

    public void collect(ItemDomainStore store) {
        List<ItemStack> items = ItemList.items;
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack != null && stack.getItem() != null) {
                ItemVariantRow row = store.register(stack);
                store.addListEntry(row.getItemVariantId(), i);
            }
        }
    }
}
