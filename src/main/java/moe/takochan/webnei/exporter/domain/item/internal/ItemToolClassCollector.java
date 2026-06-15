package moe.takochan.webnei.exporter.domain.item.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.item.model.ItemToolClassRow;

/** 默认 Item API 实现的工具类型采集器。 */
public final class ItemToolClassCollector {

    public List<ItemToolClassRow> collect(String datasetId, ItemVariantIdentity variant, ItemStack stack) {
        Item item = stack.getItem();
        Set<String> toolClasses = item.getToolClasses(stack);
        List<String> sorted = new ArrayList<>(toolClasses);
        Collections.sort(sorted);

        List<ItemToolClassRow> rows = new ArrayList<>();
        for (String toolClass : sorted) {
            rows.add(
                new ItemToolClassRow(
                    datasetId,
                    variant.getItemVariantId(),
                    toolClass,
                    item.getHarvestLevel(stack, toolClass)));
        }
        return rows;
    }
}
