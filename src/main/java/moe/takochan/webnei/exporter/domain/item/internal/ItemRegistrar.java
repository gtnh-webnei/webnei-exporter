package moe.takochan.webnei.exporter.domain.item.internal;

import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.item.hook.ItemVariantHookRegistry;
import moe.takochan.webnei.exporter.domain.item.model.ItemListEntryRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemToolClassRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.engine.store.IDomainRegistrar;

public final class ItemRegistrar implements IDomainRegistrar {

    private final String datasetId;
    private final ForgeItemIdentityResolver identityResolver = new ForgeItemIdentityResolver();
    private final ItemDetailCollector itemDetailCollector = new ItemDetailCollector();
    private final ItemVariantCollector itemVariantCollector = new ItemVariantCollector();
    private final ItemToolClassCollector toolClassCollector = new ItemToolClassCollector();
    private final ItemVariantHookRegistry itemVariantHooks = new ItemVariantHookRegistry();
    private final ItemDomainData data;

    public ItemRegistrar(ItemDomainData data, String datasetId) {
        this.data = data;
        this.datasetId = datasetId;
    }

    /** Returns whether the stack's item has a stable registry identity. */
    public boolean hasStableIdentity(ItemStack stack) {
        return identityResolver.hasStableIdentity(stack.getItem());
    }

    /** Registers only the registry-level item identity, without creating an item variant. */
    public ItemRow getOrRegisterItem(ItemStack input) {
        ItemStack stack = input.copy();
        ItemIdentity itemIdentity = identityResolver.resolveItem(stack.getItem());
        return getOrRegisterItem(itemIdentity, stack);
    }

    public ItemVariantRow getOrRegisterVariant(ItemStack input) {
        ItemStack stack = input.copy();
        ItemIdentity itemIdentity = identityResolver.resolveItem(stack.getItem());
        ItemVariantIdentity variantIdentity = identityResolver.resolveVariant(stack, itemIdentity);

        ItemVariantRow variant = data.findVariant(variantIdentity.getItemVariantId());
        if (variant != null) {
            return variant;
        }

        getOrRegisterItem(itemIdentity, stack);

        ItemVariantRow row = itemVariantCollector.collectVariant(datasetId, variantIdentity, stack);
        itemVariantHooks.enrich(stack, row);
        data.putVariant(row, stack, itemVariantCollector.collectTooltipSnapshots(datasetId, variantIdentity, stack));
        for (ItemToolClassRow toolClassRow : toolClassCollector.collect(datasetId, variantIdentity, stack)) {
            data.putToolClass(toolClassRow);
        }
        return row;
    }

    private ItemRow getOrRegisterItem(ItemIdentity itemIdentity, ItemStack stack) {
        ItemRow existing = data.findItem(itemIdentity.getItemId());
        if (existing != null) {
            return existing;
        }
        ItemRow row = itemDetailCollector.collectItem(datasetId, itemIdentity, stack);
        data.putItem(row);
        return row;
    }

    void registerListEntry(ItemStack stack, int listIndex) {
        ItemVariantRow row = getOrRegisterVariant(stack);
        data.putListEntry(new ItemListEntryRow(datasetId, row.getItemVariantId(), listIndex));
    }
}
