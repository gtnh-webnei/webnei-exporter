package moe.takochan.webnei.exporter.nei.scan;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
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
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public final class NeiHandlerScanner {

    public List<NeiHandlerDescriptor> scan() {
        List<NeiHandlerDescriptor> descriptors = new ArrayList<>();
        for (NeiHandlerEntry entry : scanEntries()) {
            descriptors.add(entry.descriptor);
        }
        return descriptors;
    }

    public List<NeiHandlerEntry> scanEntries() {
        List<NeiHandlerEntry> entries = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        int index = 0;
        index = scanList(entries, seen, index, "crafting", GuiCraftingRecipe.craftinghandlers);
        index = scanList(entries, seen, index, "serial_crafting", GuiCraftingRecipe.serialCraftingHandlers);
        index = scanList(entries, seen, index, "usage", GuiUsageRecipe.usagehandlers);
        scanList(entries, seen, index, "serial_usage", GuiUsageRecipe.serialUsageHandlers);
        return ensureUniqueCategoryIds(entries);
    }

    private List<NeiHandlerEntry> ensureUniqueCategoryIds(List<NeiHandlerEntry> entries) {
        Map<String, Integer> counts = new HashMap<>();
        for (NeiHandlerEntry entry : entries) {
            counts.put(
                entry.descriptor.resolvedCategoryId,
                counts.getOrDefault(entry.descriptor.resolvedCategoryId, 0) + 1);
        }
        List<NeiHandlerEntry> out = new ArrayList<>(entries.size());
        for (NeiHandlerEntry entry : entries) {
            NeiHandlerDescriptor descriptor = entry.descriptor;
            if (counts.getOrDefault(descriptor.resolvedCategoryId, 0) <= 1) {
                out.add(entry);
                continue;
            }
            out.add(
                new NeiHandlerEntry(
                    copyWithCategoryId(
                        descriptor,
                        descriptor.resolvedCategoryId + "." + simpleClassName(descriptor.handlerClass)),
                    entry.handler));
        }
        return out;
    }

    private static NeiHandlerDescriptor copyWithCategoryId(NeiHandlerDescriptor descriptor, String categoryId) {
        return new NeiHandlerDescriptor(
            descriptor.registrationIndex,
            descriptor.sourceList,
            descriptor.stableKey,
            descriptor.handlerClass,
            descriptor.handlerId,
            descriptor.overlayId,
            descriptor.recipeName,
            descriptor.recipeTabName,
            categoryId,
            descriptor.modId,
            descriptor.modName,
            descriptor.iconStackId,
            descriptor.catalystKey,
            descriptor.loadedRecipeCount,
            descriptor.extractionStatus,
            descriptor.reason);
    }

    private static String simpleClassName(String className) {
        int index = className.lastIndexOf('.');
        return sanitizeSegment(index < 0 ? className : className.substring(index + 1));
    }

    private int scanList(List<NeiHandlerEntry> entries, Set<String> seen, int index, String sourceList,
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
            entries.add(
                new NeiHandlerEntry(
                    scanOne(index++, sourceList, stableKey, handler, handlerClass, handlerId, overlayId),
                    handler));
        }
        return index;
    }

    private NeiHandlerDescriptor scanOne(int index, String sourceList, String stableKey, IRecipeHandler handler,
        String handlerClass, String handlerId, String overlayId) {
        HandlerInfo info = safeHandlerInfo(handler);
        String recipeName = safeRecipeName(handler);
        String recipeTabName = safeRecipeTabName(handler);
        ModIdentity modIdentity = resolveModIdentity(handler.getClass());
        String modId = normalizeUnknown(info == null ? "" : nullToEmpty(info.getModId()));
        if (modId.isEmpty()) {
            modId = modIdentity.modId;
        }
        String resolvedCategoryId = resolveCategoryId(modId, handlerId, overlayId, handlerClass);
        String modName = normalizeUnknown(info == null ? "" : nullToEmpty(info.getModName()));
        if (modName.isEmpty()) {
            modName = modIdentity.modName;
        }
        String iconStackId = info == null ? "" : itemStackId(info.getItemStack());
        String catalystKey = safeCatalystKey(handler);
        int loadedRecipeCount = -1;
        String extractionStatus = "registered";
        String reason = "recipe loading is checked in slot extraction phase";
        return new NeiHandlerDescriptor(
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

    private static ModIdentity resolveModIdentity(Class<?> handlerClass) {
        ModIdentity packageMatch = resolveModIdentityByPackage(handlerClass);
        if (!packageMatch.isEmpty()) return packageMatch;

        File classSource = classSource(handlerClass);
        if (classSource == null) return ModIdentity.EMPTY;
        File canonicalClassSource = canonicalFile(classSource);
        for (ModContainer container : Loader.instance()
            .getModList()) {
            File modSource = container.getSource();
            if (modSource == null) continue;
            if (sameFile(canonicalClassSource, canonicalFile(modSource))) {
                return new ModIdentity(normalizeUnknown(container.getModId()), normalizeUnknown(container.getName()));
            }
        }
        return ModIdentity.EMPTY;
    }

    private static ModIdentity resolveModIdentityByPackage(Class<?> handlerClass) {
        String handlerPackage = packageName(handlerClass.getName());
        ModIdentity bestIdentity = ModIdentity.EMPTY;
        int bestScore = 0;
        for (ModContainer container : Loader.instance()
            .getModList()) {
            Object mod = safeModInstance(container);
            if (mod == null) continue;
            String modPackage = packageName(
                mod.getClass()
                    .getName());
            int score = commonPackageSegments(handlerPackage, modPackage);
            if (score > bestScore && isUsablePackageMatch(handlerPackage, modPackage, score)) {
                bestScore = score;
                bestIdentity = new ModIdentity(
                    normalizeUnknown(container.getModId()),
                    normalizeUnknown(container.getName()));
            }
        }
        return bestIdentity;
    }

    private static Object safeModInstance(ModContainer container) {
        try {
            return container.getMod();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static String packageName(String className) {
        int index = className.lastIndexOf('.');
        return index < 0 ? "" : className.substring(0, index);
    }

    private static int commonPackageSegments(String left, String right) {
        if (left.isEmpty() || right.isEmpty()) return 0;
        String[] leftSegments = left.split("\\.");
        String[] rightSegments = right.split("\\.");
        int limit = Math.min(leftSegments.length, rightSegments.length);
        int count = 0;
        while (count < limit && leftSegments[count].equals(rightSegments[count])) {
            count++;
        }
        return count;
    }

    private static boolean isUsablePackageMatch(String handlerPackage, String modPackage, int score) {
        if (score >= 2) return true;
        if (score != 1) return false;
        String root = handlerPackage
            .substring(0, handlerPackage.indexOf('.') < 0 ? handlerPackage.length() : handlerPackage.indexOf('.'));
        return !"com".equals(root) && !"net".equals(root)
            && !"org".equals(root)
            && !"java".equals(root)
            && !"javax".equals(root)
            && !"cpw".equals(root)
            && modPackage.startsWith(root);
    }

    private static File classSource(Class<?> handlerClass) {
        try {
            CodeSource codeSource = handlerClass.getProtectionDomain()
                .getCodeSource();
            if (codeSource == null) return null;
            URL location = codeSource.getLocation();
            if (location == null) return null;
            return new File(location.toURI());
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static File canonicalFile(File file) {
        try {
            return file.getCanonicalFile();
        } catch (Throwable ignored) {
            return file.getAbsoluteFile();
        }
    }

    private static boolean sameFile(File left, File right) {
        return left.equals(right);
    }

    private static String normalizeUnknown(String value) {
        String normalized = nullToEmpty(value).trim();
        return "Unknown".equals(normalized) ? "" : normalized;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static final class ModIdentity {

        private static final ModIdentity EMPTY = new ModIdentity("", "");

        private final String modId;
        private final String modName;

        private ModIdentity(String modId, String modName) {
            this.modId = modId;
            this.modName = modName;
        }

        private boolean isEmpty() {
            return modId.isEmpty() && modName.isEmpty();
        }
    }
}
