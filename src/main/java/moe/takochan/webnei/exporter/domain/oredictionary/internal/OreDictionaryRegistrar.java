package moe.takochan.webnei.exporter.domain.oredictionary.internal;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import codechicken.nei.ItemList;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;
import moe.takochan.webnei.exporter.engine.store.IDomainRegistrar;

/**
 * 处理 Forge OreDictionary 数据并写入 ore_dictionary domain store。
 */
public final class OreDictionaryRegistrar implements IDomainRegistrar {

    private final OreDictionaryDomainData data;
    private final ItemDomainStore itemStore;

    public OreDictionaryRegistrar(OreDictionaryDomainData data, ItemDomainStore itemStore) {
        this.data = data;
        this.itemStore = itemStore;
    }

    /**
     * 注册一个 dictionary name 及其 Forge 返回的 ItemStack 列表。
     */
    void register(String dictionaryName, List<ItemStack> stacks) {
        data.putDictionary(dictionaryName);
        for (ItemStack stack : stacks) {
            registerStack(dictionaryName, stack);
        }
    }

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

    private void registerWildcard(String dictionaryName, Item item) {
        for (ItemStack stack : ItemList.itemMap.get(item)) {
            if (stack != null && stack.getItem() != null) {
                registerActualStack(dictionaryName, stack);
            }
        }
    }

    private void registerActualStack(String dictionaryName, ItemStack stack) {
        ItemVariantRow row = itemStore.registrar()
            .getOrRegisterVariant(stack);
        data.putEntry(dictionaryName, row.getItemVariantId());
    }
}
