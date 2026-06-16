package moe.takochan.webnei.exporter.hook.gregtech;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.Loader;
import gregtech.api.enums.Mods;
import moe.takochan.webnei.exporter.domain.item.hook.IItemVariantEnrichmentHook;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;

/**
 * 当 GregTech 加载时，从 GT/GT++/BartWorks 材料系统提取化学式写入 chemical_expression。
 */
public final class GregTechChemicalExpressionHook implements IItemVariantEnrichmentHook {

    /**
     * 检查 GregTech 是否已加载。
     *
     * @return true 表示 chemical_expression hook 可用
     */
    @Override
    public boolean isAvailable() {
        return Loader.isModLoaded(Mods.GregTech.ID);
    }

    /**
     * 提取 ItemStack 对应材料的化学式并写入 row。
     *
     * @param stack 当前正在注册的 ItemStack
     * @param row   基础字段已填充的 variant 行
     */
    @Override
    public void enrich(ItemStack stack, ItemVariantRow row) {
        String expression = GregTechChemicalExpressionExtractor.itemExpression(stack);
        if (!expression.isEmpty()) {
            row.setChemicalExpression(expression);
        }
    }
}
