package moe.takochan.webnei.exporter.domain.item.internal;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;

/**
 * 使用 Minecraft ItemStack API 采集展示字段。
 *
 * <p>
 * tooltip 使用普通玩家 tooltip，不导出高级调试 tooltip。display_name 和 tooltip_text 保留原始格式码（§x）。
 * 与 NEI {@code GuiContainerManager.itemDisplayNameShort/itemDisplayName} 一致：单个 stack 在读取 display name 或 tooltip
 * 时抛异常（例如 forestry 个体 species 为 null）不影响整体导出，回退到占位文本。
 */
public final class ItemVariantCollector {

    /** 与 NEI 渲染失败时一致的占位显示名。 */
    private static final String UNNAMED = "Unnamed";

    public ItemVariantRow collectVariant(String datasetId, ItemVariantIdentity variant, ItemStack stack) {
        return new ItemVariantRow(
            datasetId,
            variant.getItemVariantId(),
            variant.getItemId(),
            variant.getDamage(),
            variant.getNbtHash(),
            variant.getNbtText(),
            displayName(stack),
            tooltipText(stack));
    }

    private static String displayName(ItemStack stack) {
        try {
            return value(stack.getDisplayName());
        } catch (Throwable ignored) {
            return UNNAMED;
        }
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
                builder.append(value(tooltip.get(i)));
            }
            return builder.toString();
        } catch (Throwable ignored) {
            return "";
        }
    }

    private static String value(String value) {
        return value == null ? "" : value;
    }
}
