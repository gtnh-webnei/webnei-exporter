package moe.takochan.webnei.exporter.adapter.gregtech;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

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
import gtPlusPlus.core.item.base.BaseItemComponent;
import gtPlusPlus.core.item.base.ore.BaseOreComponent;
import gtPlusPlus.core.material.Material;

/** 从 GregTech/GT++/BartWorks 的真实 API 读取 item chemical expression */
final class GregTechChemicalExpressionExtractor {

    private GregTechChemicalExpressionExtractor() {}

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

    private static String gtPlusPlusMaterialExpression(Material material) {
        if (material == null || material.vChemicalFormula == null || material.vChemicalFormula.isEmpty()) {
            return "";
        }
        String formula = material.vChemicalFormula;
        String sanitized = formula.contains("?") ? StringUtils.sanitizeStringKeepBracketsQuestion(formula)
            : StringUtils.sanitizeStringKeepBrackets(formula);
        return clean(sanitized);
    }

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

    private static String clean(String value) {
        String cleaned = EnumChatFormatting.getTextWithoutFormattingCodes(value);
        return cleaned == null ? "" : cleaned.trim();
    }
}
