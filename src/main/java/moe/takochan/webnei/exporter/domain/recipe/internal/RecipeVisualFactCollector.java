package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.WebneiExporterMod;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidContainerRow;
import moe.takochan.webnei.exporter.domain.fluid.store.FluidDomainStore;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;

/** Collects ordered recipe slots and item candidates from public NEI data. */
public final class RecipeVisualFactCollector {

    private static final int ORIGINAL_CANDIDATE_INDEX = -1;

    private final ItemDomainStore itemStore;
    private final FluidDomainStore fluidStore;

    public RecipeVisualFactCollector(ItemDomainStore itemStore, FluidDomainStore fluidStore) {
        this.itemStore = itemStore;
        this.fluidStore = fluidStore;
    }

    /** Extracts one recipe page; returns null when no reliable slots can be collected. */
    public RecipeVisualObservation collect(IRecipeHandler handler, int recipeIndex, RecipeCategoryIdentity identity) {
        List<RecipeSlotObservation> inputs = collectSlots(
            handler,
            recipeIndex,
            safeIngredients(handler, recipeIndex),
            identity);
        RecipeSlotObservation result = collectSlot(handler, recipeIndex, safeResult(handler, recipeIndex), identity);
        List<RecipeSlotObservation> others = collectSlots(
            handler,
            recipeIndex,
            safeOthers(handler, recipeIndex),
            identity);
        if (inputs.isEmpty() && result == null && others.isEmpty()) {
            return null;
        }
        return RecipeVisualObservation.of(inputs, result, others);
    }

    private List<RecipeSlotObservation> collectSlots(IRecipeHandler handler, int recipeIndex,
        List<PositionedStack> stacks, RecipeCategoryIdentity identity) {
        if (stacks == null || stacks.isEmpty()) {
            return Collections.emptyList();
        }
        List<RecipeSlotObservation> out = new ArrayList<>(stacks.size());
        for (PositionedStack stack : stacks) {
            RecipeSlotObservation slot = collectSlot(handler, recipeIndex, stack, identity);
            if (slot != null) {
                out.add(slot);
            }
        }
        return out;
    }

    private RecipeSlotObservation collectSlot(IRecipeHandler handler, int recipeIndex, PositionedStack stack,
        RecipeCategoryIdentity identity) {
        if (stack == null) {
            return null;
        }
        List<RecipeCandidateObservation> candidates = collectCandidates(handler, recipeIndex, stack);
        if (candidates.isEmpty()) {
            return null;
        }
        return RecipeSlotObservation.of(stack.relx, stack.rely + identity.getYShift(), candidates);
    }

    private List<RecipeCandidateObservation> collectCandidates(IRecipeHandler handler, int recipeIndex,
        PositionedStack stack) {
        List<RecipeCandidateObservation> out = new ArrayList<>();
        forEachActiveCandidate(stack, (sourceIndex, active) -> {
            RecipeCandidateObservation candidate = resolveCandidate(handler, recipeIndex, stack, sourceIndex, active);
            if (candidate != null) {
                out.add(candidate);
            }
        });
        return out;
    }

    static void forEachActiveCandidate(PositionedStack stack, ActiveCandidateConsumer consumer) {
        ItemStack original = stack.item;
        boolean activatedCandidate = false;
        try {
            if (stack.items != null) {
                for (int index = 0; index < stack.items.length; index++) {
                    ItemStack candidate = stack.items[index];
                    if (!isValid(candidate)) {
                        continue;
                    }
                    try {
                        stack.setPermutationToRender(index);
                        activatedCandidate = true;
                        consumer.accept(index, stack.item);
                    } finally {
                        stack.item = original;
                    }
                }
            }
            if (!activatedCandidate && isValid(original)) {
                try {
                    stack.item = original;
                    consumer.accept(ORIGINAL_CANDIDATE_INDEX, stack.item);
                } finally {
                    stack.item = original;
                }
            }
        } finally {
            stack.item = original;
        }
    }

    interface ActiveCandidateConsumer {

        void accept(int sourceIndex, ItemStack active);
    }

    private RecipeCandidateObservation resolveCandidate(IRecipeHandler handler, int recipeIndex,
        PositionedStack positionedStack, int sourceIndex, ItemStack stack) {
        if (!itemStore.hasStableIdentity(stack)) {
            WebneiExporterMod.LOG.warn(
                "Skipping recipe candidate with unregistered item: handlerClass={}, recipeIndex={}, relx={}, rely={}, sourceCandidateIndex={} (items[] index; -1 means original fallback), itemClass={}",
                handler.getClass()
                    .getName(),
                recipeIndex,
                positionedStack.relx,
                positionedStack.rely,
                sourceIndex,
                stack.getItem()
                    .getClass()
                    .getName());
            return null;
        }

        FluidContainerRow container = fluidStore.registrar()
            .registerContainer(stack);
        String variantId = container != null ? container.getItemVariantId()
            : itemStore.registrar()
                .getOrRegisterVariant(stack)
                .getItemVariantId();
        return new RecipeCandidateObservation(variantId, stack.stackSize);
    }

    private static boolean isValid(ItemStack stack) {
        return stack != null && stack.getItem() != null;
    }

    private static List<PositionedStack> safeIngredients(IRecipeHandler handler, int recipeIndex) {
        try {
            return handler.getIngredientStacks(recipeIndex);
        } catch (Throwable ignored) {
            return Collections.emptyList();
        }
    }

    private static PositionedStack safeResult(IRecipeHandler handler, int recipeIndex) {
        try {
            return handler.getResultStack(recipeIndex);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static List<PositionedStack> safeOthers(IRecipeHandler handler, int recipeIndex) {
        try {
            return handler.getOtherStacks(recipeIndex);
        } catch (Throwable ignored) {
            return Collections.emptyList();
        }
    }
}
