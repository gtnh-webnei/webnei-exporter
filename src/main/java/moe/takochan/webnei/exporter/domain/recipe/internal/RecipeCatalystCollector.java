package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.RecipeCatalysts;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryCatalystRow;

/**
 * 采集能打开某个配方分类的物品（NEI catalyst），产出 recipe_category_catalyst 行。
 *
 * <p>
 * 来源是 NEI {@link RecipeCatalysts#getRecipeCatalysts(IRecipeHandler)}，返回的 list 已按 catalyst priority 排好序；
 * 同一格 cycling 的多个候选物品全部展开。catalyst 物品的 item variant 通过 item store 解析并补齐，与 fluid domain
 * 解析容器 variant 的方式一致。collector 只产出行，去重由 {@link RecipeDomainData} 负责。
 */
public final class RecipeCatalystCollector {

    private final ItemDomainStore itemStore;

    public RecipeCatalystCollector(ItemDomainStore itemStore) {
        this.itemStore = itemStore;
    }

    /**
     * 采集该 handler 对应分类的全部 catalyst 行；没有 catalyst 时返回空列表。
     */
    public List<RecipeCategoryCatalystRow> collect(String datasetId, String categoryId, IRecipeHandler handler) {
        Set<String> variantIds = resolveVariantIds(fetchCatalysts(handler));
        return buildRows(datasetId, categoryId, variantIds);
    }

    /**
     * 把 catalyst 格子展开成去重后的 item variant id 集合。
     *
     * <p>
     * 一个格子可能是 cycling（多个候选物品轮播），全部展开；用 LinkedHashSet 在保持 catalyst priority 顺序的同时去重。
     */
    private Set<String> resolveVariantIds(List<PositionedStack> catalysts) {
        Set<String> variantIds = new LinkedHashSet<>();
        for (PositionedStack catalyst : catalysts) {
            if (catalyst == null || catalyst.items == null) {
                continue;
            }
            for (ItemStack stack : catalyst.items) {
                if (stack != null && stack.getItem() != null) {
                    variantIds.add(variantId(stack));
                }
            }
        }
        return variantIds;
    }

    /**
     * 按 variant 顺序建行，display_order 从 0 递增，保留 catalyst 在 NEI 中的展示次序。
     */
    private static List<RecipeCategoryCatalystRow> buildRows(String datasetId, String categoryId,
                                                             Set<String> variantIds) {
        List<RecipeCategoryCatalystRow> rows = new ArrayList<>();
        int displayOrder = 0;
        for (String itemVariantId : variantIds) {
            rows.add(new RecipeCategoryCatalystRow(datasetId, categoryId, itemVariantId, displayOrder++));
        }
        return rows;
    }

    /**
     * 向 NEI 取该 handler 的 catalyst 列表；NEI 内部异常或无 catalyst 时返回空列表。
     */
    private static List<PositionedStack> fetchCatalysts(IRecipeHandler handler) {
        try {
            List<PositionedStack> catalysts = RecipeCatalysts.getRecipeCatalysts(handler);
            return catalysts == null ? Collections.<PositionedStack>emptyList() : catalysts;
        } catch (RuntimeException e) {
            return Collections.emptyList();
        }
    }


    /**
     * 经 item store 解析并按需补登记 variant，拿到稳定的 item_variant_id。
     */
    private String variantId(ItemStack stack) {
        return itemStore.registrar()
            .getOrRegisterVariant(stack)
            .getItemVariantId();
    }
}
