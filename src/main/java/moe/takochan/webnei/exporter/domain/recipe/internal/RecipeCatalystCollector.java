package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.ArrayList;
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
 * 同一格 cycling 的多个候选物品全部展开。catalyst 物品的 item variant 通过 item store 解析并补齐，与
 * {@link FluidContainerCollector} 解析容器 variant 的方式一致。collector 只产出行，去重由 {@code Data} 负责。
 */
public final class RecipeCatalystCollector {

    private final ItemDomainStore itemStore;

    public RecipeCatalystCollector(ItemDomainStore itemStore) {
        this.itemStore = itemStore;
    }

    /** 采集该 handler 对应分类的全部 catalyst 行；没有 catalyst 时返回空列表。 */
    public List<RecipeCategoryCatalystRow> collect(String datasetId, String categoryId, IRecipeHandler handler) {
        List<PositionedStack> catalysts;
        try {
            catalysts = RecipeCatalysts.getRecipeCatalysts(handler);
        } catch (RuntimeException e) {
            return new ArrayList<>();
        }
        if (catalysts == null || catalysts.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> variantIds = new LinkedHashSet<>();
        for (PositionedStack catalyst : catalysts) {
            if (catalyst == null || catalyst.items == null) {
                continue;
            }
            for (ItemStack stack : catalyst.items) {
                if (stack != null && stack.getItem() != null) {
                    variantIds.add(
                        itemStore.getOrRegisterVariant(stack)
                            .getItemVariantId());
                }
            }
        }

        List<RecipeCategoryCatalystRow> rows = new ArrayList<>();
        int displayOrder = 0;
        for (String itemVariantId : variantIds) {
            rows.add(new RecipeCategoryCatalystRow(datasetId, categoryId, itemVariantId, displayOrder++));
        }
        return rows;
    }
}
