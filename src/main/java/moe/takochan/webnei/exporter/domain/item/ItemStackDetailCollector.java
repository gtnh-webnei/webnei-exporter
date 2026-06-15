package moe.takochan.webnei.exporter.domain.item;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import moe.takochan.webnei.exporter.domain.dataset.task.DatasetIdentity;
import moe.takochan.webnei.exporter.domain.item.model.ItemRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;

/**
 * 使用 Minecraft ItemStack API 采集展示字段。
 *
 * <p>
 * tooltip 使用普通玩家 tooltip，不导出高级调试 tooltip；display/tooltip 会去掉格式码，避免把 UI 控制字符写入搜索字段。
 */
public final class ItemStackDetailCollector implements IItemDetailCollector {

    @Override
    public ItemRow collectItem(DatasetIdentity dataset, ItemIdentity item, ItemStack stack) {
        Item mcItem = stack.getItem();
        return new ItemRow(
            dataset.getDatasetId(),
            item.getItemId(),
            item.getModId(),
            item.getRegistryName(),
            value(stack.getUnlocalizedName()),
            stack.getMaxStackSize(),
            stack.getMaxDamage(),
            Item.getIdFromItem(mcItem));
    }

    @Override
    public ItemVariantRow collectVariant(DatasetIdentity dataset, ItemVariantIdentity variant, ItemStack stack,
        String assetId) {
        return new ItemVariantRow(
            dataset.getDatasetId(),
            variant.getItemVariantId(),
            variant.getItemId(),
            variant.getDamage(),
            variant.getNbtHash(),
            variant.getNbtText(),
            stripFormatting(value(stack.getDisplayName())),
            tooltipText(stack),
            assetId);
    }

    private static String tooltipText(ItemStack stack) {
        try {
            @SuppressWarnings("unchecked")
            List<String> tooltip = stack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < tooltip.size(); i++) {
                if (i > 0) {
                    builder.append('\n');
                }
                builder.append(stripFormatting(value(tooltip.get(i))));
            }
            return builder.toString();
        } catch (RuntimeException e) {
            return "";
        }
    }

    private static String stripFormatting(String value) {
        return EnumChatFormatting.getTextWithoutFormattingCodes(value);
    }

    private static String value(String value) {
        return value == null ? "" : value;
    }
}
