package moe.takochan.webnei.exporter.domain.fluid.internal;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import codechicken.nei.ItemList;
import codechicken.nei.recipe.StackInfo;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidContainerRow;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;

/**
 * 采集承载流体的容器，产出 fluid_container 行。
 *
 * <p>
 * 构造时从 NEI item universe 扫描“item stack 承载流体”的事实，按流体缓存为 variant ID -> 数量（mB）；同一套解析也对外提供单个容器
 * stack 的解析（{@link #resolveFluid} / {@link #collectOne}），供配方等后续流程反向补充。StackInfo 会统一识别 Forge 简单容器、
 * IFluidContainerItem、GT fluid display 和 AE2FC fluid stack 表示物。
 */
public final class FluidContainerCollector {

    private final ItemDomainStore itemStore;
    private final Map<Fluid, Map<String, Integer>> amountsByFluid = new IdentityHashMap<>();

    public FluidContainerCollector(ItemDomainStore itemStore) {
        this.itemStore = itemStore;
        for (ItemStack stack : ItemList.items) {
            FluidStack fluidStack = parse(stack);
            if (fluidStack == null) {
                continue;
            }
            String itemVariantId = itemStore.registrar()
                .getOrRegisterVariant(stack)
                .getItemVariantId();
            amountsByFluid.computeIfAbsent(fluidStack.getFluid(), key -> new LinkedHashMap<>())
                .putIfAbsent(itemVariantId, fluidStack.amount);
        }
    }

    /** 采集既定扫描发现的、承载该流体的全部容器行。 */
    public List<FluidContainerRow> collect(String datasetId, String fluidId, Fluid fluid) {
        List<FluidContainerRow> rows = new ArrayList<>();
        Map<String, Integer> amounts = amountsByFluid.get(fluid);
        if (amounts == null) {
            return rows;
        }
        for (Map.Entry<String, Integer> entry : amounts.entrySet()) {
            rows.add(new FluidContainerRow(datasetId, fluidId, entry.getValue(), entry.getKey()));
        }
        return rows;
    }

    /** 解析单个 stack 承载的流体；非流体容器返回 {@code null}。 */
    public Fluid resolveFluid(ItemStack stack) {
        FluidStack fluidStack = parse(stack);
        return fluidStack == null ? null : fluidStack.getFluid();
    }

    /** 解析单个流体容器 stack 为容器行；非流体容器返回 {@code null}。 */
    public FluidContainerRow collectOne(String datasetId, String fluidId, ItemStack stack) {
        FluidStack fluidStack = parse(stack);
        if (fluidStack == null) {
            return null;
        }
        String itemVariantId = itemStore.registrar()
            .getOrRegisterVariant(stack)
            .getItemVariantId();
        return new FluidContainerRow(datasetId, fluidId, fluidStack.amount, itemVariantId);
    }

    private static FluidStack parse(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return null;
        }
        FluidStack fluidStack = StackInfo.getFluid(stack);
        if (fluidStack == null || fluidStack.getFluid() == null) {
            return null;
        }
        return fluidStack;
    }
}
