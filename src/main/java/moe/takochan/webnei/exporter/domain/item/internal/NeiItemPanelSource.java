package moe.takochan.webnei.exporter.domain.item.internal;

import java.util.List;

import net.minecraft.item.ItemStack;

import codechicken.nei.ItemList;

/**
 * 读取 NEI 当前 item panel 列表。
 *
 * <p>
 * {@code ItemList.items} 是展示源。每个 stack 注册为 item/variant/toolclass 后，再记录展示顺序。
 */
public final class NeiItemPanelSource {

    private final ItemRegistrar registrar;

    public NeiItemPanelSource(ItemRegistrar registrar) {
        this.registrar = registrar;
    }

    public void collect() {
        List<ItemStack> items = ItemList.items;
        // 使用普通for循环保证顺序，并将index指定为order
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack != null && stack.getItem() != null) {
                this.registrar.registerListEntry(stack, i);
            }
        }
    }
}
