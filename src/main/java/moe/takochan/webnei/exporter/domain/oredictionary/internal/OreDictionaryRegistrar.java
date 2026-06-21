package moe.takochan.webnei.exporter.domain.oredictionary.internal;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import codechicken.nei.ItemList;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;

/**
 * 处理 Forge OreDictionary 数据并写入 ore_dictionary domain store。
 */
public final class OreDictionaryRegistrar {

    private final OreDictionaryDomainData data;
    private final ItemDomainStore itemStore;

    public OreDictionaryRegistrar(OreDictionaryDomainData data, ItemDomainStore itemStore) {
        this.data = data;
        this.itemStore = itemStore;
    }

    /**
     * 注册一个 dictionary name 及其 Forge 返回的 ItemStack 列表。
     */
    public void register(String dictionaryName, List<ItemStack> stacks) {
        data.registerDictionary(dictionaryName);
        for (ItemStack stack : stacks) {
            registerStack(dictionaryName, stack);
        }
    }

    /**
     * 注册单个 ore entry；普通 stack 直接注册，wildcard damage 先展开成实际 ItemStack。
     */
    private void registerStack(String dictionaryName, ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return;
        }
        if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
            registerWildcard(dictionaryName, stack.getItem());
            return;
        }
        registerActualStack(dictionaryName, stack);
    }

    /**
     * 按 NEI 当前 item universe 将 wildcard damage 展开为实际 ItemStack。
     */
    private void registerWildcard(String dictionaryName, Item item) {
        for (ItemStack stack : ItemList.itemMap.get(item)) {
            if (stack != null && stack.getItem() != null) {
                registerActualStack(dictionaryName, stack);
            }
        }
    }

    /**
     * 将实际 ItemStack 交给 item store 获取/补齐 variant，并写入矿物词典关联。
     */
    private void registerActualStack(String dictionaryName, ItemStack stack) {
        ItemVariantRow row = itemStore.getOrRegisterVariant(stack);
        data.registerEntry(dictionaryName, row.getItemVariantId());
    }
}
