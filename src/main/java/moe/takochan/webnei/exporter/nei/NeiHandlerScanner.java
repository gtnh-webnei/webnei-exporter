package moe.takochan.webnei.exporter.nei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiRecipeTab;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.HandlerInfo;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.RecipeCatalysts;

public final class NeiHandlerScanner {

    public List<HandlerScanRow> scan() {
        List<HandlerScanRow> rows = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        int index = 0;
        index = scanList(rows, seen, index, "crafting", GuiCraftingRecipe.craftinghandlers);
        index = scanList(rows, seen, index, "serial_crafting", GuiCraftingRecipe.serialCraftingHandlers);
        index = scanList(rows, seen, index, "usage", GuiUsageRecipe.usagehandlers);
        scanList(rows, seen, index, "serial_usage", GuiUsageRecipe.serialUsageHandlers);
        return ensureUniqueCategoryIds(rows);
    }

    private List<HandlerScanRow> ensureUniqueCategoryIds(List<HandlerScanRow> rows) {
        Map<String, Integer> counts = new HashMap<>();
        for (HandlerScanRow row : rows) {
            counts.put(row.resolvedCategoryId, counts.getOrDefault(row.resolvedCategoryId, 0) + 1);
        }
        List<HandlerScanRow> out = new ArrayList<>(rows.size());
        for (HandlerScanRow row : rows) {
            if (counts.getOrDefault(row.resolvedCategoryId, 0) <= 1) {
                out.add(row);
                continue;
            }
            out.add(copyWithCategoryId(row, row.resolvedCategoryId + "." + simpleClassName(row.handlerClass)));
        }
        return out;
    }

    private static HandlerScanRow copyWithCategoryId(HandlerScanRow row, String categoryId) {
        return new HandlerScanRow(
            row.registrationIndex,
            row.sourceList,
            row.stableKey,
            row.handlerClass,
            row.handlerId,
            row.overlayId,
            row.recipeName,
            row.recipeTabName,
            categoryId,
            row.modId,
            row.modName,
            row.iconStackId,
            row.catalystKey,
            row.loadedRecipeCount,
            row.extractionStatus,
            row.reason);
    }

    private static String simpleClassName(String className) {
        int index = className.lastIndexOf('.');
        return sanitizeSegment(index < 0 ? className : className.substring(index + 1));
    }

    private int scanList(List<HandlerScanRow> rows, Set<String> seen, int index, String sourceList,
        List<? extends IRecipeHandler> handlers) {
        for (IRecipeHandler handler : handlers) {
            String handlerClass = handler.getClass()
                .getName();
            String handlerId = safeHandlerId(handler);
            String overlayId = safeOverlayId(handler);
            String stableKey = handlerClass + "::" + handlerId + "::" + overlayId;
            if (!seen.add(stableKey)) {
                continue;
            }
            rows.add(scanOne(index++, sourceList, stableKey, handler, handlerClass, handlerId, overlayId));
        }
        return index;
    }

    private HandlerScanRow scanOne(int index, String sourceList, String stableKey, IRecipeHandler handler,
        String handlerClass, String handlerId, String overlayId) {
        HandlerInfo info = safeHandlerInfo(handler);
        String recipeName = safeRecipeName(handler);
        String recipeTabName = safeRecipeTabName(handler);
        String modId = info == null ? "" : nullToEmpty(info.getModId());
        String resolvedCategoryId = resolveCategoryId(modId, handlerId, overlayId, handlerClass);
        String modName = info == null ? "" : nullToEmpty(info.getModName());
        String iconStackId = info == null ? "" : itemStackId(info.getItemStack());
        String catalystKey = safeCatalystKey(handler);
        int loadedRecipeCount = -1;
        String extractionStatus = "registered";
        String reason = "recipe loading is checked in slot extraction phase";
        return new HandlerScanRow(
            index,
            sourceList,
            stableKey,
            handlerClass,
            handlerId,
            overlayId,
            recipeName,
            recipeTabName,
            resolvedCategoryId,
            modId,
            modName,
            iconStackId,
            catalystKey,
            loadedRecipeCount,
            extractionStatus,
            reason);
    }

    private static String resolveCategoryId(String modId, String handlerId, String overlayId, String handlerClass) {
        String namespace = sanitizeSegment(modId.isEmpty() || "Unknown".equals(modId) ? "unknown" : modId);
        String local;
        if (!overlayId.isEmpty() && !isGenericOverlayId(overlayId)) {
            local = overlayId;
        } else if (!handlerId.isEmpty() && !isGenericOverlayId(handlerId)) {
            local = handlerId;
        } else {
            local = handlerClass;
        }
        return namespace + ":" + sanitizeSegment(local);
    }

    private static boolean isGenericOverlayId(String id) {
        return "crafting".equals(id) || "item".equals(id);
    }

    private static String sanitizeSegment(String value) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = Character.toLowerCase(value.charAt(i));
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                out.append(c);
            } else if (c == ':' || c == '.' || c == '_' || c == '-') {
                out.append(c == ':' ? '.' : c);
            } else {
                out.append('_');
            }
        }
        return out.length() == 0 ? "unknown" : out.toString();
    }

    private static HandlerInfo safeHandlerInfo(IRecipeHandler handler) {
        try {
            return GuiRecipeTab.getHandlerInfo(handler);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static String safeHandlerId(IRecipeHandler handler) {
        try {
            return nullToEmpty(handler.getHandlerId());
        } catch (Throwable ignored) {
            return "";
        }
    }

    private static String safeOverlayId(IRecipeHandler handler) {
        try {
            return nullToEmpty(handler.getOverlayIdentifier());
        } catch (Throwable ignored) {
            return "";
        }
    }

    private static String safeRecipeName(IRecipeHandler handler) {
        try {
            return nullToEmpty(handler.getRecipeName()).trim();
        } catch (Throwable t) {
            return "";
        }
    }

    private static String safeRecipeTabName(IRecipeHandler handler) {
        try {
            return nullToEmpty(handler.getRecipeTabName()).trim();
        } catch (Throwable t) {
            return "";
        }
    }

    private static String safeCatalystKey(IRecipeHandler handler) {
        try {
            return nullToEmpty(RecipeCatalysts.getRecipeID(handler));
        } catch (Throwable ignored) {
            return "";
        }
    }

    private static String itemStackId(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return "";
        return String.valueOf(
            stack.getItem()
                .getUnlocalizedName(stack))
            + "@"
            + stack.getItemDamage();
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
