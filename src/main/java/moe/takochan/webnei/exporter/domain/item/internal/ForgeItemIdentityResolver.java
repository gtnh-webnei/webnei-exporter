package moe.takochan.webnei.exporter.domain.item.internal;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import moe.takochan.webnei.exporter.WebneiExporterMod;
import moe.takochan.webnei.exporter.util.StableHash;
import moe.takochan.webnei.exporter.util.StableNbtText;

/** 基于 Forge registry 和 ItemStack 字段解析稳定身份。 */
public final class ForgeItemIdentityResolver {

    /** 解析物品身份，优先用 Forge registry，失败时回退 itemRegistry 名称。 */
    public ItemIdentity resolveItem(Item item) {
        UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(item);
        if (identifier != null) {
            return new ItemIdentity(identifier.toString(), identifier.modId, identifier.name);
        }

        // 正常应命中 Forge registry；这里只处理异常回退。
        String itemId = String.valueOf(Item.itemRegistry.getNameForObject(item));
        WebneiExporterMod.LOG
            .warn("Unexpected fallback to itemRegistry name for item identity: item={}, itemId={}", item, itemId);
        int separator = itemId.indexOf(':');
        if (separator > 0) {
            return new ItemIdentity(itemId, itemId.substring(0, separator), itemId.substring(separator + 1));
        }
        // 无可靠 mod id 时留空，与 recipe/mod domain 的空值规则一致。
        return new ItemIdentity(itemId, "", itemId);
    }

    /** 解析 ItemStack 变体身份。 */
    public ItemVariantIdentity resolveVariant(ItemStack stack, ItemIdentity item) {
        // 读取 raw damage，避免物品自定义逻辑影响结果。
        int damage = Items.feather.getDamage(stack);
        String nbtText = StableNbtText.of(stack.getTagCompound());
        String nbtHash = StableHash.shortHash(nbtText);
        String itemVariantId = item.getItemId() + "@" + damage + (nbtHash.isEmpty() ? "" : "#" + nbtHash);
        return new ItemVariantIdentity(itemVariantId, item.getItemId(), damage, nbtHash, nbtText);
    }
}
