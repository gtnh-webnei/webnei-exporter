package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidContainerRow;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidRow;
import moe.takochan.webnei.exporter.domain.fluid.store.FluidDisplayResolution;
import moe.takochan.webnei.exporter.domain.fluid.store.FluidDomainStore;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;
import moe.takochan.webnei.exporter.domain.recipe.hook.ExtraRecipeSlot;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeSlotSourceHookRegistry;

/**
 * 从已经可枚举配方的 NEI handler 中，按 recipeIndex 提取一个配方页面的 visual facts。
 *
 * <p>
 * 只使用 NEI 公开接口（getIngredientStacks / getResultStack / getOtherStacks）和 PositionedStack 的公共字段。 每个 stack
 * 候选分两类处理：被流体 hook 命中的 NEI 流体显示占位物按 fluid 候选导出； 其余按 item 候选导出，同时把 stack 交给 fluid store 顺手识别并写入
 * fluid_container 表。slot_key / candidate_order / recipe_id 等稳定 ID 不在此处分配，由 {@link RecipeDomainData} 统一负责。
 *
 * <p>
 * 标准三接口之外，{@link RecipeSlotSourceHookRegistry} 命中的 handler 会补充额外格子（如 Tinkers' Construct 流体罐），
 * 这些格子带钩子明确指定的 role，流体候选在此处解析成稳定 fluid_id。
 */
public final class RecipeVisualFactCollector {

    private static final String TARGET_DOMAIN_ITEM = "item";
    private static final String TARGET_DOMAIN_FLUID = "fluid";

    private final ItemDomainStore itemStore;
    private final FluidDomainStore fluidStore;
    private final RecipeSlotSourceHookRegistry slotSourceHooks;

    public RecipeVisualFactCollector(ItemDomainStore itemStore, FluidDomainStore fluidStore,
        RecipeSlotSourceHookRegistry slotSourceHooks) {
        this.itemStore = itemStore;
        this.fluidStore = fluidStore;
        this.slotSourceHooks = slotSourceHooks;
    }

    /**
     * 抽取一个配方页面的 visual facts。NEI handler 异常时返回 null，调用方据此跳过该 recipe。
     *
     * @param handler     已加载的 handler
     * @param recipeIndex handler 内的 recipe 顺序索引
     * @param identity    配方分类身份，用于读取 yShift 折算坐标
     */
    public RecipeVisualObservation collect(IRecipeHandler handler, int recipeIndex, RecipeCategoryIdentity identity) {
        List<RecipeSlotObservation> inputs = collectSlots(safeIngredients(handler, recipeIndex), identity);
        RecipeSlotObservation result = collectSlot(safeResult(handler, recipeIndex), identity);
        List<RecipeSlotObservation> others = collectSlots(safeOthers(handler, recipeIndex), identity);
        List<RecipeSlotObservation> extraInputs = new ArrayList<>();
        List<RecipeSlotObservation> extraOutputs = new ArrayList<>();
        collectExtraSlots(handler, recipeIndex, identity, extraInputs, extraOutputs);
        if (inputs.isEmpty() && result == null && others.isEmpty() && extraInputs.isEmpty() && extraOutputs.isEmpty()) {
            return null;
        }
        return RecipeVisualObservation.of(inputs, result, others, extraInputs, extraOutputs);
    }

    /** 问格子来源钩子拿额外格子，按 role 拆进 extraInputs / extraOutputs，并把流体候选解析成 fluid_id。 */
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

    /** 把钩子交出的原始 {@link FluidStack} 候选解析成稳定 fluid 候选；解析失败的单项丢弃。 */
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
        try {
            FluidRow row = fluidStore.registrar()
                .getOrRegisterFluid(fluid);
            return new RecipeCandidateObservation(TARGET_DOMAIN_FLUID, row.getFluidId(), fluidStack.amount);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private List<RecipeSlotObservation> collectSlots(List<PositionedStack> stacks, RecipeCategoryIdentity identity) {
        if (stacks == null || stacks.isEmpty()) {
            return Collections.emptyList();
        }
        List<RecipeSlotObservation> out = new ArrayList<>(stacks.size());
        for (PositionedStack stack : stacks) {
            RecipeSlotObservation slot = collectSlot(stack, identity);
            if (slot != null) {
                out.add(slot);
            }
        }
        return out;
    }

    private RecipeSlotObservation collectSlot(PositionedStack stack, RecipeCategoryIdentity identity) {
        if (stack == null) {
            return null;
        }
        List<RecipeCandidateObservation> candidates = collectCandidates(stack);
        if (candidates.isEmpty()) {
            return null;
        }
        return RecipeSlotObservation.of(stack.relx, stack.rely + identity.getYShift(), candidates);
    }

    private List<RecipeCandidateObservation> collectCandidates(PositionedStack stack) {
        List<ItemStack> source = candidateStacks(stack);
        if (source.isEmpty()) {
            return Collections.emptyList();
        }
        List<RecipeCandidateObservation> out = new ArrayList<>(source.size());
        for (ItemStack candidate : source) {
            RecipeCandidateObservation observation = resolveCandidate(candidate);
            if (observation != null) {
                out.add(observation);
            }
        }
        return out;
    }

    /**
     * 把一个候选 stack 解析为 candidate observation。
     *
     * <p>
     * 优先级：
     * <ol>
     * <li>流体显示占位物（如 GT Display_Fluid）：fluid hook 命中 → fluid 候选。</li>
     * <li>流体容器物品：fluid store 注册 fluid_container 时已经顺手登记了 item variant，直接复用返回的 item_variant_id。</li>
     * <li>普通 item：item store 注册 variant 拿 item_variant_id。</li>
     * </ol>
     * 单个 stack 解析整体抛异常（例如底层 mod 数据损坏）时返回 null，调用方丢弃该候选；与 NEI {@code safeItemRenderContext}
     * 把渲染失败的坏 stack 隔离一致。
     */
    private RecipeCandidateObservation resolveCandidate(ItemStack stack) {
        try {
            FluidDisplayResolution fluid = fluidStore.registrar()
                .tryAsFluidDisplay(stack);
            if (fluid != null) {
                return new RecipeCandidateObservation(TARGET_DOMAIN_FLUID, fluid.getFluidId(), fluid.getAmount());
            }
            FluidContainerRow container = fluidStore.registrar()
                .registerContainer(stack);
            String variantId = container != null ? container.getItemVariantId()
                : itemStore.registrar()
                    .getOrRegisterVariant(stack)
                    .getItemVariantId();
            return new RecipeCandidateObservation(TARGET_DOMAIN_ITEM, variantId, stack.stackSize);
        } catch (Throwable ignored) {
            return null;
        }
    }

    /** 优先取候选数组 items；为空时 fallback 到 PositionedStack.item。 */
    private static List<ItemStack> candidateStacks(PositionedStack stack) {
        List<ItemStack> out = new ArrayList<>();
        if (stack.items != null) {
            for (ItemStack item : stack.items) {
                if (item != null && item.getItem() != null) {
                    out.add(item);
                }
            }
        }
        if (out.isEmpty() && stack.item != null && stack.item.getItem() != null) {
            out.add(stack.item);
        }
        return out;
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
