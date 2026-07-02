package moe.takochan.webnei.exporter.domain.item.internal;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.item.model.ItemRow;

/**
 * 采集 item 基础字段。
 *
 * <p>
 * 与 NEI 处理坏 ItemStack 一致：单个 stack 在读取 unlocalized name / stack size / damage 时抛异常（例如 forestry 个体 species
 * 为 null）不影响整体导出，回退到安全占位值。
 */
public class ItemDetailCollector {

    /** stack 抛异常时 max_stack_size 的安全占位（vanilla 默认上限）。 */
    private static final int DEFAULT_MAX_STACK_SIZE = 64;

    public ItemRow collectItem(String datasetId, ItemIdentity item, ItemStack stack) {
        Item mcItem = stack.getItem();
        return new ItemRow(
            datasetId,
            item.getItemId(),
            item.getModId(),
            item.getRegistryName(),
            unlocalizedName(stack),
            maxStackSize(stack),
            maxDamage(stack),
            Item.getIdFromItem(mcItem));
    }

    private static String unlocalizedName(ItemStack stack) {
        try {
            return value(stack.getUnlocalizedName());
        } catch (Throwable ignored) {
            return "";
        }
    }

    private static int maxStackSize(ItemStack stack) {
        try {
            return stack.getMaxStackSize();
        } catch (Throwable ignored) {
            return DEFAULT_MAX_STACK_SIZE;
        }
    }

    private static int maxDamage(ItemStack stack) {
        try {
            return stack.getMaxDamage();
        } catch (Throwable ignored) {
            return 0;
        }
    }

    private static String value(String value) {
        return value == null ? "" : value;
    }
}
