package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.WebneiExporterMod;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidContainerRow;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidRow;
import moe.takochan.webnei.exporter.domain.fluid.store.FluidDisplayResolution;
import moe.takochan.webnei.exporter.domain.fluid.store.FluidDomainStore;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;
import moe.takochan.webnei.exporter.domain.recipe.hook.ExtraRecipeSlot;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeCandidateMetadata;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeCandidateMetadataHookRegistry;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeSlotSourceHookRegistry;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeTooltipRegionHookRegistry;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeTooltipRegionObservation;

/** Collects ordered recipe slots, candidates, and non-slot tooltip regions from public NEI data. */
public final class RecipeVisualFactCollector {

    private static final int ORIGINAL_CANDIDATE_INDEX = -1;

    private final ItemDomainStore itemStore;
    private final FluidDomainStore fluidStore;
    private final RecipeSlotSourceHookRegistry slotSourceHooks;
    private final RecipeCandidateMetadataHookRegistry candidateMetadataHooks;
    private final RecipeTooltipRegionHookRegistry tooltipRegionHooks;

    public RecipeVisualFactCollector(ItemDomainStore itemStore, FluidDomainStore fluidStore,
        RecipeSlotSourceHookRegistry slotSourceHooks, RecipeCandidateMetadataHookRegistry candidateMetadataHooks,
        RecipeTooltipRegionHookRegistry tooltipRegionHooks) {
        this.itemStore = itemStore;
        this.fluidStore = fluidStore;
        this.slotSourceHooks = slotSourceHooks;
        this.candidateMetadataHooks = candidateMetadataHooks;
        this.tooltipRegionHooks = tooltipRegionHooks;
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
        List<RecipeSlotObservation> extraInputs = new ArrayList<>();
        List<RecipeSlotObservation> extraOutputs = new ArrayList<>();
        collectExtraSlots(handler, recipeIndex, identity, extraInputs, extraOutputs);
        if (inputs.isEmpty() && result == null && others.isEmpty() && extraInputs.isEmpty() && extraOutputs.isEmpty()) {
            return null;
        }
        List<RecipeTooltipRegionObservation> regions = collectRegions(handler, recipeIndex, identity.getYShift());
        return RecipeVisualObservation.of(inputs, result, others, extraInputs, extraOutputs, regions);
    }

    private List<RecipeTooltipRegionObservation> collectRegions(IRecipeHandler handler, int recipeIndex, int yShift) {
        List<RecipeTooltipRegionObservation> collected = tooltipRegionHooks.collect(handler, recipeIndex);
        List<RecipeTooltipRegionObservation> shifted = new ArrayList<>(collected.size());
        for (RecipeTooltipRegionObservation region : collected) {
            shifted.add(region.withYShift(yShift));
        }
        return shifted;
    }

    private void collectExtraSlots(IRecipeHandler handler, int recipeIndex, RecipeCategoryIdentity identity,
        List<RecipeSlotObservation> extraInputs, List<RecipeSlotObservation> extraOutputs) {
        if (!slotSourceHooks.hasSource(handler)) {
            return;
        }
        for (ExtraRecipeSlot extra : slotSourceHooks.extractSlots(handler, recipeIndex)) {
            List<RecipeCandidateObservation> candidates = resolveFluidCandidates(extra.getFluidCandidates());
            if (candidates.isEmpty()) {
                continue;
            }
            RecipeSlotObservation slot = RecipeSlotObservation
                .of(extra.getX(), extra.getY() + identity.getYShift(), candidates);
            if (extra.getRole() == ExtraRecipeSlot.Role.INPUT) {
                extraInputs.add(slot);
            } else {
                extraOutputs.add(slot);
            }
        }
    }

    private List<RecipeCandidateObservation> resolveFluidCandidates(List<FluidStack> fluidStacks) {
        List<RecipeCandidateObservation> out = new ArrayList<>(fluidStacks.size());
        for (FluidStack fluidStack : fluidStacks) {
            RecipeCandidateObservation candidate = resolveFluidCandidate(fluidStack);
            if (candidate != null) {
                out.add(candidate);
            }
        }
        return out;
    }

    private RecipeCandidateObservation resolveFluidCandidate(FluidStack fluidStack) {
        if (fluidStack == null) {
            return null;
        }
        Fluid fluid = fluidStack.getFluid();
        if (fluid == null) {
            return null;
        }
        FluidRow row = fluidStore.registrar()
            .getOrRegisterFluid(fluid);
        return RecipeCandidateObservations.fluidSlot(row.getFluidId(), fluidStack.amount);
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
            RecipeCandidateMetadata metadata = candidateMetadataHooks.collect(handler, recipeIndex, stack);
            RecipeCandidateObservation candidate = resolveCandidate(
                handler,
                recipeIndex,
                stack,
                sourceIndex,
                active,
                metadata);
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
        PositionedStack positionedStack, int sourceIndex, ItemStack stack, RecipeCandidateMetadata metadata) {
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

        FluidDisplayResolution fluid = fluidStore.registrar()
            .tryAsFluidDisplay(stack);
        if (fluid != null) {
            String carrierVariantId = itemStore.registrar()
                .getOrRegisterVariant(stack)
                .getItemVariantId();
            return RecipeCandidateObservations.fluidDisplay(
                fluid.getFluidId(),
                fluid.getAmount(),
                fluid.getPresentationType(),
                carrierVariantId,
                fluid.getAmountUnit(),
                metadata);
        }
        FluidContainerRow container = fluidStore.registrar()
            .registerContainer(stack);
        String variantId = container != null ? container.getItemVariantId()
            : itemStore.registrar()
                .getOrRegisterVariant(stack)
                .getItemVariantId();
        return RecipeCandidateObservations.itemStack(variantId, stack.stackSize, metadata);
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
