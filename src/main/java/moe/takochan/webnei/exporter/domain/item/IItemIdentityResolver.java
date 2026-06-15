package moe.takochan.webnei.exporter.domain.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * 把 Minecraft/Forge 对象解析成 WebNEI 稳定身份。
 *
 * <p>
 * ItemStack 身份必须按 registry item id + raw meta + canonical NBT 计算，不能用 Java 对象引用做匹配。
 */
public interface IItemIdentityResolver {

    ItemIdentity resolveItem(Item item);

    ItemVariantIdentity resolveVariant(ItemStack stack);
}
