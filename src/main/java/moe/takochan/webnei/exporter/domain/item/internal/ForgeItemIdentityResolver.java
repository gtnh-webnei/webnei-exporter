package moe.takochan.webnei.exporter.domain.item.internal;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import moe.takochan.webnei.exporter.util.StableHash;
import moe.takochan.webnei.exporter.util.StableNbtText;

/**
 * 基于 Forge registry 和 ItemStack 原始字段实现稳定身份解析。
 *
 * <p>
 * registry id 使用 GameRegistry.findUniqueIdentifierFor；damage 使用 raw ItemStack damage，避免被 Item 覆写后的逻辑值影响；
 * NBT 使用 canonical 文本和 hash 参与 variant id。
 */
public final class ForgeItemIdentityResolver {

    public ItemIdentity resolveItem(Item item) {
        UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(item);
        if (identifier != null) {
            return new ItemIdentity(identifier.toString(), identifier.modId, identifier.name);
        }

        String itemId = String.valueOf(Item.itemRegistry.getNameForObject(item));
        int separator = itemId.indexOf(':');
        if (separator > 0) {
            return new ItemIdentity(itemId, itemId.substring(0, separator), itemId.substring(separator + 1));
        }
        return new ItemIdentity(itemId, "", itemId);
    }

    public ItemVariantIdentity resolveVariant(ItemStack stack) {
        ItemIdentity item = resolveItem(stack.getItem());
        int damage = Items.feather.getDamage(stack);
        String nbtText = StableNbtText.of(stack.getTagCompound());
        String nbtHash = StableHash.shortHash(nbtText);
        String itemVariantId = item.getItemId() + "@" + damage + (nbtHash.isEmpty() ? "" : "~" + nbtHash);
        return new ItemVariantIdentity(itemVariantId, item.getItemId(), damage, nbtHash, nbtText);
    }
}
