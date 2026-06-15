package moe.takochan.webnei.exporter.domain.item.internal;

import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.adapter.AdapterContext;
import moe.takochan.webnei.exporter.adapter.AdapterRegistry;
import moe.takochan.webnei.exporter.domain.item.model.ItemToolClassRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;

/**
 * item 注册处理器 — 负责身份解析、去重、字段采集、adapter 补充。
 *
 * <p>
 * 处理完后将结果写入 {@link ItemDomainStore}。
 */
public final class ItemRegistrar {

    private final ItemDomainStore store;
    private final ForgeItemIdentityResolver identityResolver;
    private final ItemStackDetailCollector detailCollector;
    private final ItemToolClassCollector toolClassCollector;
    private final AdapterRegistry adapterRegistry;
    private final AdapterContext adapterContext;
    private final Set<String> toolClassKeys = new LinkedHashSet<>();

    public ItemRegistrar(ItemDomainStore store) {
        this.store = store;
        this.identityResolver = new ForgeItemIdentityResolver();
        this.detailCollector = new ItemStackDetailCollector();
        this.toolClassCollector = new ItemToolClassCollector();
        this.adapterRegistry = AdapterRegistry.defaults();
        this.adapterContext = new AdapterContext();
    }

    public ItemVariantRow register(ItemStack input) {
        ItemStack copy = input.copy();
        ItemIdentity itemIdentity = identityResolver.resolveItem(copy.getItem());
        ItemVariantIdentity variantIdentity = identityResolver.resolveVariant(copy);
        ensureItem(itemIdentity, copy);
        return ensureVariant(itemIdentity, variantIdentity, copy);
    }

    private void ensureItem(ItemIdentity identity, ItemStack stack) {
        if (!store.containsItem(identity.getItemId())) {
            store.putItem(identity.getItemId(), detailCollector.collectItem(store.datasetId(), identity, stack));
        }
    }

    private ItemVariantRow ensureVariant(ItemIdentity itemIdentity, ItemVariantIdentity variantIdentity,
        ItemStack stack) {
        ItemVariantRow existing = store.getVariant(variantIdentity.getItemVariantId());
        if (existing != null) {
            return existing;
        }
        ItemVariantRow row = detailCollector.collectVariant(store.datasetId(), variantIdentity, stack);
        adapterRegistry.fillItemVariant(stack, row, adapterContext);
        store.putVariant(variantIdentity.getItemVariantId(), row, stack);
        addToolClasses(variantIdentity, stack);
        return row;
    }

    private void addToolClasses(ItemVariantIdentity variant, ItemStack stack) {
        for (ItemToolClassRow row : toolClassCollector.collect(store.datasetId(), variant, stack)) {
            String key = row.getItemVariantId() + '\u0000' + row.getToolClass();
            if (toolClassKeys.add(key)) {
                store.addToolClass(row);
            }
        }
    }
}
