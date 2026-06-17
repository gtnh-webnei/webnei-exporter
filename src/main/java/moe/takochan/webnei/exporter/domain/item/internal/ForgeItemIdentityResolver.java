package moe.takochan.webnei.exporter.domain.item.internal;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import moe.takochan.webnei.exporter.WebneiExporterMod;
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

    private static final String UNKNOW_ITEM_MODID = "unknow";

    /**
     * 解析物品的稳定基础身份。
     *
     * <p>
     * 优先使用 Forge registry 中的 {@link UniqueIdentifier}，确保 modId 和 name 与注册表保持一致；
     * 如果 Forge 未返回标识，则退回到原版 itemRegistry 名称，并按 {@code modid:name} 拆分命名空间。
     *
     * @param item 待解析的物品实例
     * @return 包含完整 itemId、modId 和本地名称的物品身份
     */
    public ItemIdentity resolveItem(Item item) {
        UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(item);
        if (identifier != null) {
            return new ItemIdentity(identifier.toString(), identifier.modId, identifier.name);
        }

        // fallback：正常情况下 Forge GameRegistry 应能取得 UniqueIdentifier；命中此分支说明存在特殊物品或注册表解析异常。
        String itemId = String.valueOf(Item.itemRegistry.getNameForObject(item));
        WebneiExporterMod.LOG
            .warn("Unexpected fallback to itemRegistry name for item identity: item={}, itemId={}", item, itemId);
        int separator = itemId.indexOf(':');
        if (separator > 0) {
            return new ItemIdentity(itemId, itemId.substring(0, separator), itemId.substring(separator + 1));
        }
        return new ItemIdentity(itemId, UNKNOW_ITEM_MODID, itemId);
    }

    /**
     * 基于已解析的物品身份解析 ItemStack 的稳定变体身份，避免调用方重复解析 item。
     *
     * @param stack 待解析的物品堆
     * @param item  已解析的物品身份
     * @return 包含 itemId、damage、NBT hash 和 NBT 文本的物品变体身份
     */
    public ItemVariantIdentity resolveVariant(ItemStack stack, ItemIdentity item) {
        // 通过任意未覆写 damage 逻辑的原版物品读取 raw ItemStack damage，避免 stack.getItemDamage() 被物品自定义逻辑影响。
        int damage = Items.feather.getDamage(stack);
        String nbtText = StableNbtText.of(stack.getTagCompound());
        String nbtHash = StableHash.shortHash(nbtText);
        String itemVariantId = item.getItemId() + "@" + damage + (nbtHash.isEmpty() ? "" : "~" + nbtHash);
        return new ItemVariantIdentity(itemVariantId, item.getItemId(), damage, nbtHash, nbtText);
    }
}
