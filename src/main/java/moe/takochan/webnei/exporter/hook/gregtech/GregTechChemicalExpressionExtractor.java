package moe.takochan.webnei.exporter.hook.gregtech;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidStack;

import bartworks.system.material.BWItemMetaGeneratedBlock;
import bartworks.system.material.BWMetaGeneratedItems;
import bartworks.system.material.Werkstoff;
import bartworks.system.material.gtenhancement.BWGTMetaItems;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.objects.ItemData;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.StringUtils;
import gregtech.common.blocks.ItemOres;
import gregtech.common.items.ItemFluidDisplay;
import gtPlusPlus.core.item.base.BaseItemComponent;
import gtPlusPlus.core.item.base.ore.BaseOreComponent;
import gtPlusPlus.core.material.Material;

/**
 * 从 GregTech/GT++/BartWorks 的真实 API 读取 item chemical expression。
 */
final class GregTechChemicalExpressionExtractor {

    private GregTechChemicalExpressionExtractor() {}

    /**
     * 按 GT++ → BartWorks → GT 矿石 → GT 通用顺序尝试提取化学式，命中即返回。
     *
     * @param stack 当前正在注册的 ItemStack
     * @return 化学式，未识别时为空字符串
     */
    static String itemExpression(ItemStack stack) {
        String gtpp = gtPlusPlusItemExpression(stack);
        if (!gtpp.isEmpty()) {
            return gtpp;
        }
        String bartWorks = bartWorksWerkstoffExpression(stack);
        if (!bartWorks.isEmpty()) {
            return bartWorks;
        }
        String gregTechOre = gregTechOreBlockExpression(stack);
        if (!gregTechOre.isEmpty()) {
            return gregTechOre;
        }
        return gregTechItemExpression(stack);
    }

    /**
     * 从 GT fluid display API 获取流体化学式。
     *
     * @param stack 当前正在注册的 FluidStack
     * @return 化学式，未识别时为空字符串
     */
    static String fluidExpression(FluidStack stack) {
        try {
            return clean(ItemFluidDisplay.getChemicalFormula(stack));
        } catch (Throwable ignored) {
            return "";
        }
    }

    /**
     * 通过 GT OreDict 关联获取通用材料化学式。
     *
     * @param stack 当前正在注册的 ItemStack
     * @return 化学式，未识别时为空字符串
     */
    private static String gregTechItemExpression(ItemStack stack) {
        try {
            ItemData association = GTOreDictUnificator.getAssociation(stack);
            if (association == null || !association.hasValidPrefixMaterialData()) {
                return "";
            }
            Materials material = association.mMaterial.mMaterial;
            long materialAmount = association.mPrefix.mMaterialAmount;
            long multiplier = materialAmount <= 0 ? 1 : materialAmount / GTValues.M;
            if (multiplier <= 0) {
                multiplier = 1;
            }
            return clean(material.getToolTip(multiplier));
        } catch (Throwable ignored) {
            return "";
        }
    }

    /**
     * 从 GT++ BaseItemComponent / BaseOreComponent 获取化学式。
     *
     * @param stack 当前正在注册的 ItemStack
     * @return 化学式，未识别时为空字符串
     */
    private static String gtPlusPlusItemExpression(ItemStack stack) {
        try {
            Item item = stack.getItem();
            if (item instanceof BaseItemComponent baseItem) {
                return gtPlusPlusMaterialExpression(baseItem.componentMaterial);
            }
            if (item instanceof BaseOreComponent baseOre) {
                return gtPlusPlusMaterialExpression(baseOre.componentMaterial);
            }
            return "";
        } catch (Throwable ignored) {
            return "";
        }
    }

    /**
     * 从 GT++ Material 对象读取 vChemicalFormula 并清理特殊字符。
     *
     * @param material GT++ 材料对象
     * @return 化学式，未识别时为空字符串
     */
    private static String gtPlusPlusMaterialExpression(Material material) {
        if (material == null || material.vChemicalFormula == null || material.vChemicalFormula.isEmpty()) {
            return "";
        }
        String formula = material.vChemicalFormula;
        String sanitized = formula.contains("?") ? StringUtils.sanitizeStringKeepBracketsQuestion(formula)
            : StringUtils.sanitizeStringKeepBrackets(formula);
        return clean(sanitized);
    }

    /**
     * 从 BartWorks BWGTMetaItems / BWMetaGeneratedItems / BWItemMetaGeneratedBlock 获取 Werkstoff 化学式。
     *
     * @param stack 当前正在注册的 ItemStack
     * @return 化学式，未识别时为空字符串
     */
    private static String bartWorksWerkstoffExpression(ItemStack stack) {
        try {
            Item item = stack.getItem();
            if (item instanceof BWGTMetaItems bwGtMetaItem) {
                Materials material = bwGtMetaItem.getMaterialFromMeta(stack.getItemDamage());
                return material == null ? "" : clean(material.getToolTip());
            }
            if (item instanceof BWMetaGeneratedItems || item instanceof BWItemMetaGeneratedBlock) {
                Werkstoff werkstoff = Werkstoff.werkstoffHashMap.get(Short.valueOf((short) stack.getItemDamage()));
                return werkstoff == null ? "" : clean(werkstoff.getToolTip());
            }
            return "";
        } catch (Throwable ignored) {
            return "";
        }
    }

    /**
     * 从 GT 矿石方块的 damage % 1000 索引 sGeneratedMaterials 获取化学式。
     *
     * @param stack 当前正在注册的 ItemStack
     * @return 化学式，未识别时为空字符串
     */
    private static String gregTechOreBlockExpression(ItemStack stack) {
        try {
            if (!(stack.getItem() instanceof ItemOres)) {
                return "";
            }
            int materialIndex = stack.getItemDamage() % 1000;
            if (materialIndex < 0 || materialIndex >= GregTechAPI.sGeneratedMaterials.length) {
                return "";
            }
            Materials material = GregTechAPI.sGeneratedMaterials[materialIndex];
            return material == null ? "" : clean(material.getToolTip());
        } catch (Throwable ignored) {
            return "";
        }
    }

    /**
     * 去除 Minecraft 格式码并 trim。
     *
     * @param value 原始文本
     * @return 清理后的文本，null 时返回空字符串
     */
    private static String clean(String value) {
        String cleaned = EnumChatFormatting.getTextWithoutFormattingCodes(value);
        return cleaned == null ? "" : cleaned.trim();
    }
}
