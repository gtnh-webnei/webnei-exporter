package moe.takochan.webnei.exporter.domain.item.internal;

import net.minecraft.item.ItemStack;

public final class ItemRegistrar {

    private final ItemDomainData data;

    public ItemRegistrar(ItemDomainData data) {
        this.data = data;
    }

    public void registerListEntry(ItemStack stack, int listIndex) {
        data.registerListEntry(stack, listIndex);
    }
}
