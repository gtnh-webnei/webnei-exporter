package moe.takochan.webnei.exporter.domain.item.internal;

import java.util.Iterator;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;

/**
 * Forge item registry 的基础 ItemStack 种子来源。
 *
 * <p>
 * 这里只能为每个注册 Item 构造代表性 stack；它不是完整变体 universe。当前 item 阶段优先用 NEI panel source，
 * 后续如需要 registry fallback 再接入此 source。
 */
public final class ForgeItemTypeSource {

    public void collect(ItemDomainStore store) {
        Iterator<?> iterator = Item.itemRegistry.iterator();
        while (iterator.hasNext()) {
            Object value = iterator.next();
            if (value instanceof Item item) {
                store.add(new ItemStack(item, 1, 0));
            }
        }
    }
}
