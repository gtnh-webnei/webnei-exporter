package moe.takochan.webnei.exporter.domain.item.internal;

import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.item.hook.ItemVariantHookRegistry;
import moe.takochan.webnei.exporter.domain.item.model.ItemToolClassRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;

/**
 * item 注册处理器 — 负责身份解析、去重、字段采集、hook 补充。
 *
 * <p>
 * 处理完后将结果写入 {@link ItemDomainStore}。
 */
public final class ItemRegistrar {

    private final ItemDomainStore store;
    private final ForgeItemIdentityResolver identityResolver;
    private final ItemStackDetailCollector detailCollector;
    private final ItemToolClassCollector toolClassCollector;
    private final ItemVariantHookRegistry itemVariantHooks;
    private final Set<String> toolClassKeys = new LinkedHashSet<>();

    public ItemRegistrar(ItemDomainStore store) {
        this.store = store;
        this.identityResolver = new ForgeItemIdentityResolver();
        this.detailCollector = new ItemStackDetailCollector();
        this.toolClassCollector = new ItemToolClassCollector();
        this.itemVariantHooks = new ItemVariantHookRegistry();
    }

    /**
     * 注册一个 ItemStack：解析身份、去重、采集字段、调用 hook、写入 store。
     *
     * @param input 原始 ItemStack，内部会 copy 后操作
     * @return 对应的 ItemVariantRow（可能是已有的）
     */
    public ItemVariantRow register(ItemStack input) {
        ItemStack copy = input.copy();
        ItemIdentity itemIdentity = identityResolver.resolveItem(copy.getItem());
        ItemVariantIdentity variantIdentity = identityResolver.resolveVariant(copy);
        ensureItem(itemIdentity, copy);
        return ensureVariant(itemIdentity, variantIdentity, copy);
    }

    /**
     * 首次遇到该 item 时采集基础信息写入 store。
     */
    private void ensureItem(ItemIdentity identity, ItemStack stack) {
        if (!store.containsItem(identity.getItemId())) {
            store.putItem(identity.getItemId(), detailCollector.collectItem(store.datasetId(), identity, stack));
        }
    }

    /**
     * 首次遇到该 variant 时采集详情、执行 hook 补充、写入 store 并收集 tool class。
     */
    private ItemVariantRow ensureVariant(ItemIdentity itemIdentity, ItemVariantIdentity variantIdentity,
        ItemStack stack) {
        ItemVariantRow existing = store.getVariant(variantIdentity.getItemVariantId());
        if (existing != null) {
            return existing;
        }
        ItemVariantRow row = detailCollector.collectVariant(store.datasetId(), variantIdentity, stack);
        // item variant enrichment hook 点。
        itemVariantHooks.enrich(stack, row);
        store.putVariant(variantIdentity.getItemVariantId(), row, stack);
        addToolClasses(variantIdentity, stack);
        return row;
    }

    /**
     * 采集该 variant 的工具类型并去重写入 store。
     */
    private void addToolClasses(ItemVariantIdentity variant, ItemStack stack) {
        for (ItemToolClassRow row : toolClassCollector.collect(store.datasetId(), variant, stack)) {
            String key = row.getItemVariantId() + '\u0000' + row.getToolClass();
            if (toolClassKeys.add(key)) {
                store.addToolClass(row);
            }
        }
    }
}
