package moe.takochan.webnei.exporter.nei.recipe;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.IUsageHandler;
import moe.takochan.webnei.exporter.nei.scan.NeiHandlerDescriptor;
import moe.takochan.webnei.exporter.nei.scan.NeiHandlerEntry;

public final class StandardSlotExtractor {

    public SlotExtraction extract(List<NeiHandlerEntry> entries) {
        List<ExtractedHandler> handlers = new ArrayList<>();
        List<ExtractedRecipe> recipes = new ArrayList<>();
        List<ExtractedStack> stacks = new ArrayList<>();
        List<ExtractedCandidate> candidates = new ArrayList<>();

        for (NeiHandlerEntry entry : entries) {
            extractHandler(entry, handlers, recipes, stacks, candidates);
        }
        return new SlotExtraction(handlers, recipes, stacks, candidates);
    }

    private static void extractHandler(NeiHandlerEntry entry, List<ExtractedHandler> handlers,
        List<ExtractedRecipe> recipes, List<ExtractedStack> stacks, List<ExtractedCandidate> candidates) {
        LoadedHandler loaded = loadBestHandler(entry);
        if (loaded.handler == null) {
            handlers.add(new ExtractedHandler(entry.descriptor, -1, "error", loaded.reason));
            return;
        }

        String handlerStatus = loaded.recipeCount == 0 ? "empty" : "standard";
        handlers.add(new ExtractedHandler(entry.descriptor, loaded.recipeCount, handlerStatus, loaded.reason));
        for (int recipeIndex = 0; recipeIndex < loaded.recipeCount; recipeIndex++) {
            extractRecipe(entry.descriptor, loaded.handler, recipeIndex, recipes, stacks, candidates);
        }
    }

    private static LoadedHandler loadBestHandler(NeiHandlerEntry entry) {
        LoadedHandler best = tryLoaded(entry.handler, "registered");
        for (String key : loadingKeys(entry.descriptor)) {
            LoadedHandler candidate = tryLoadingKey(entry.handler, entry.descriptor.sourceList, key);
            if (candidate.recipeCount > best.recipeCount) {
                best = candidate;
            }
        }
        return best;
    }

    private static LoadedHandler tryLoadingKey(IRecipeHandler handler, String sourceList, String key) {
        try {
            if (isCraftingSource(sourceList) && handler instanceof ICraftingHandler) {
                return tryLoaded(((ICraftingHandler) handler).getRecipeHandler(key), "loaded key=" + key);
            }
            if (isUsageSource(sourceList) && handler instanceof IUsageHandler) {
                return tryLoaded(((IUsageHandler) handler).getUsageHandler(key), "loaded key=" + key);
            }
            return new LoadedHandler(null, -1, "unsupported handler source for key=" + key);
        } catch (Throwable t) {
            return new LoadedHandler(null, -1, "load key=" + key + " failed: " + errorReason(t));
        }
    }

    private static LoadedHandler tryLoaded(IRecipeHandler handler, String reason) {
        if (handler == null) {
            return new LoadedHandler(null, -1, reason + ": null handler");
        }
        try {
            return new LoadedHandler(handler, handler.numRecipes(), reason);
        } catch (Throwable t) {
            return new LoadedHandler(null, -1, reason + ": " + errorReason(t));
        }
    }

    private static void extractRecipe(NeiHandlerDescriptor descriptor, IRecipeHandler handler, int recipeIndex,
        List<ExtractedRecipe> recipes, List<ExtractedStack> stacks, List<ExtractedCandidate> candidates) {
        SourceStacks ingredients = getIngredientStacks(handler, recipeIndex);
        SourceStacks result = getResultStack(handler, recipeIndex);
        SourceStacks others = getOtherStacks(handler, recipeIndex);

        StringBuilder fingerprintInput = new StringBuilder();
        appendStacks(fingerprintInput, "ingredient", ingredients.stacks);
        appendStacks(fingerprintInput, "result", result.stacks);
        appendStacks(fingerprintInput, "other", others.stacks);

        extractStacks(descriptor.stableKey, recipeIndex, "ingredient", ingredients.stacks, stacks, candidates);
        extractStacks(descriptor.stableKey, recipeIndex, "result", result.stacks, stacks, candidates);
        extractStacks(descriptor.stableKey, recipeIndex, "other", others.stacks, stacks, candidates);

        String reason = joinReasons(ingredients.reason, result.reason, others.reason);
        String status = reason.isEmpty() ? "standard"
            : (ingredients.stacks.isEmpty() && result.stacks.isEmpty() && others.stacks.isEmpty() ? "error"
                : "partial");
        recipes.add(
            new ExtractedRecipe(
                descriptor.stableKey,
                recipeIndex,
                sha1(fingerprintInput.toString()),
                ingredients.stacks.size(),
                result.stacks.size(),
                others.stacks.size(),
                status,
                reason));
    }

    private static List<String> loadingKeys(NeiHandlerDescriptor descriptor) {
        List<String> keys = new ArrayList<>();
        addKey(keys, descriptor.overlayId);
        addKey(keys, descriptor.handlerId);
        addKey(keys, descriptor.catalystKey);
        addKey(keys, descriptor.recipeName);
        addKey(keys, descriptor.recipeTabName);
        int colon = descriptor.resolvedCategoryId.indexOf(':');
        if (colon >= 0 && colon + 1 < descriptor.resolvedCategoryId.length()) {
            addKey(keys, descriptor.resolvedCategoryId.substring(colon + 1));
        }
        return keys;
    }

    private static void addKey(List<String> keys, String key) {
        if (key == null || key.isEmpty()) return;
        if (!keys.contains(key)) {
            keys.add(key);
        }
    }

    private static boolean isCraftingSource(String sourceList) {
        return "crafting".equals(sourceList) || "serial_crafting".equals(sourceList);
    }

    private static boolean isUsageSource(String sourceList) {
        return "usage".equals(sourceList) || "serial_usage".equals(sourceList);
    }

    private static SourceStacks getIngredientStacks(IRecipeHandler handler, int recipeIndex) {
        try {
            return SourceStacks.ok(safeList(handler.getIngredientStacks(recipeIndex)));
        } catch (Throwable t) {
            return SourceStacks.error(errorReason(t));
        }
    }

    private static SourceStacks getResultStack(IRecipeHandler handler, int recipeIndex) {
        try {
            PositionedStack result = handler.getResultStack(recipeIndex);
            if (result == null) {
                return SourceStacks.ok(Collections.<PositionedStack>emptyList());
            }
            return SourceStacks.ok(Collections.singletonList(result));
        } catch (Throwable t) {
            return SourceStacks.error(errorReason(t));
        }
    }

    private static SourceStacks getOtherStacks(IRecipeHandler handler, int recipeIndex) {
        try {
            return SourceStacks.ok(safeList(handler.getOtherStacks(recipeIndex)));
        } catch (Throwable t) {
            return SourceStacks.error(errorReason(t));
        }
    }

    private static List<PositionedStack> safeList(List<PositionedStack> stacks) {
        if (stacks == null) {
            return Collections.emptyList();
        }
        List<PositionedStack> out = new ArrayList<>();
        for (PositionedStack stack : stacks) {
            if (stack != null) {
                out.add(stack);
            }
        }
        return out;
    }

    private static void extractStacks(String handlerKey, int recipeIndex, String source,
        List<PositionedStack> sourceStacks, List<ExtractedStack> stacks, List<ExtractedCandidate> candidates) {
        for (int stackIndex = 0; stackIndex < sourceStacks.size(); stackIndex++) {
            PositionedStack stack = sourceStacks.get(stackIndex);
            List<ItemStack> stackCandidates = candidates(stack);
            String status = stackCandidates.isEmpty() ? "empty" : "standard";
            String reason = stackCandidates.isEmpty() ? "no candidates" : "";
            stacks.add(
                new ExtractedStack(
                    handlerKey,
                    recipeIndex,
                    source,
                    stackIndex,
                    stack.relx,
                    stack.rely,
                    stackCandidates.size(),
                    stackCandidates.isEmpty() ? "" : stackId(stackCandidates.get(0)),
                    status,
                    reason));
            for (int candidateIndex = 0; candidateIndex < stackCandidates.size(); candidateIndex++) {
                ItemStack candidate = stackCandidates.get(candidateIndex);
                candidates.add(
                    new ExtractedCandidate(
                        handlerKey,
                        recipeIndex,
                        source,
                        stackIndex,
                        candidateIndex,
                        itemId(candidate),
                        candidate.getItemDamage(),
                        candidate.stackSize,
                        displayName(candidate)));
            }
        }
    }

    private static List<ItemStack> candidates(PositionedStack stack) {
        List<ItemStack> candidates = new ArrayList<>();
        if (stack.items != null) {
            for (ItemStack item : stack.items) {
                if (item != null && item.getItem() != null) {
                    candidates.add(item);
                }
            }
        }
        if (candidates.isEmpty() && stack.item != null && stack.item.getItem() != null) {
            candidates.add(stack.item);
        }
        return candidates;
    }

    private static void appendStacks(StringBuilder out, String source, List<PositionedStack> stacks) {
        out.append(source)
            .append(':');
        for (PositionedStack stack : stacks) {
            out.append(stack.relx)
                .append(',')
                .append(stack.rely)
                .append('[');
            for (ItemStack candidate : candidates(stack)) {
                out.append(stackId(candidate))
                    .append(';');
            }
            out.append(']');
        }
        out.append('|');
    }

    private static String itemId(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return "";
        Item item = stack.getItem();
        Object registryName = Item.itemRegistry.getNameForObject(item);
        return registryName == null ? item.getClass()
            .getName() : registryName.toString();
    }

    private static String stackId(ItemStack stack) {
        if (stack == null) return "";
        return itemId(stack) + "@" + stack.getItemDamage() + "x" + stack.stackSize;
    }

    private static String displayName(ItemStack stack) {
        try {
            return stack.getDisplayName();
        } catch (Throwable t) {
            return "";
        }
    }

    private static String joinReasons(String left, String middle, String right) {
        StringBuilder out = new StringBuilder();
        appendReason(out, left);
        appendReason(out, middle);
        appendReason(out, right);
        return out.toString();
    }

    private static void appendReason(StringBuilder out, String reason) {
        if (reason == null || reason.isEmpty()) return;
        if (out.length() > 0) out.append(" | ");
        out.append(reason);
    }

    private static String errorReason(Throwable t) {
        return t.getClass()
            .getSimpleName() + (t.getMessage() == null ? "" : ": " + t.getMessage());
    }

    private static String sha1(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder out = new StringBuilder();
            for (byte b : bytes) {
                out.append(String.format("%02x", b & 0xff));
            }
            return out.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(value.hashCode());
        }
    }

    private static final class LoadedHandler {

        private final IRecipeHandler handler;
        private final int recipeCount;
        private final String reason;

        private LoadedHandler(IRecipeHandler handler, int recipeCount, String reason) {
            this.handler = handler;
            this.recipeCount = recipeCount;
            this.reason = reason;
        }
    }

    private static final class SourceStacks {

        private final List<PositionedStack> stacks;
        private final String reason;

        private SourceStacks(List<PositionedStack> stacks, String reason) {
            this.stacks = stacks;
            this.reason = reason;
        }

        private static SourceStacks ok(List<PositionedStack> stacks) {
            return new SourceStacks(stacks, "");
        }

        private static SourceStacks error(String reason) {
            return new SourceStacks(Collections.<PositionedStack>emptyList(), reason);
        }
    }
}
