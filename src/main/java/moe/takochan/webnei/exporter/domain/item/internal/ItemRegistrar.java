package moe.takochan.webnei.exporter.domain.item.internal;

import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.item.hook.ItemVariantHookRegistry;
import moe.takochan.webnei.exporter.domain.item.model.ItemListEntryRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemToolClassRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.engine.store.IDomainRegistrar;

public final class ItemRegistrar implements IDomainRegistrar {

    private final String datasetId;
    private final ForgeItemIdentityResolver identityResolver = new ForgeItemIdentityResolver();
    private final ItemStackDetailCollector detailCollector = new ItemStackDetailCollector();
    private final ItemToolClassCollector toolClassCollector = new ItemToolClassCollector();
    private final ItemVariantHookRegistry itemVariantHooks = new ItemVariantHookRegistry();
    private final ItemDomainData data;

    public ItemRegistrar(ItemDomainData data, String datasetId) {
        this.data = data;
        this.datasetId = datasetId;
    }

    public ItemVariantRow getOrRegisterVariant(ItemStack input) {
        ItemStack stack = input.copy();
        ItemIdentity itemIdentity = identityResolver.resolveItem(stack.getItem());
        ItemVariantIdentity variantIdentity = identityResolver.resolveVariant(stack, itemIdentity);

        ItemVariantRow variant = data.findVariant(variantIdentity.getItemVariantId());
        if (variant != null) {
            return variant;
        }

        if (data.findItem(itemIdentity.getItemId()) == null) {
            data.putItem(detailCollector.collectItem(datasetId, itemIdentity, stack));
        }

        ItemVariantRow row = detailCollector.collectVariant(datasetId, variantIdentity, stack);
        itemVariantHooks.enrich(stack, row);
        data.putVariant(row, stack);
        for (ItemToolClassRow toolClassRow : toolClassCollector.collect(datasetId, variantIdentity, stack)) {
            data.putToolClass(toolClassRow);
        }
        return row;
    }

    void registerListEntry(ItemStack stack, int listIndex) {
        ItemVariantRow row = getOrRegisterVariant(stack);
        data.putListEntry(new ItemListEntryRow(datasetId, row.getItemVariantId(), listIndex));
    }
}
