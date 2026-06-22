package moe.takochan.webnei.exporter.domain.item.internal;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.item.model.ItemRow;

public class ItemCollector {

    public ItemRow collectItem(String datasetId, ItemIdentity item, ItemStack stack) {
        Item mcItem = stack.getItem();
        return new ItemRow(
            datasetId,
            item.getItemId(),
            item.getModId(),
            item.getRegistryName(),
            value(stack.getUnlocalizedName()),
            stack.getMaxStackSize(),
            stack.getMaxDamage(),
            Item.getIdFromItem(mcItem));
    }

    private static String value(String value) {
        return value == null ? "" : value;
    }

}
