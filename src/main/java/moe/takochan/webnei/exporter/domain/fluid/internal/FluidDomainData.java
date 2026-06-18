package moe.takochan.webnei.exporter.domain.fluid.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.fluid.FluidExportModel;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidBlockRow;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidContainerRow;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidRow;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;

/**
 * fluid domain store 的内部数据和注册逻辑。
 *
 * <p>
 * 该类挂载流体、方块、容器三个 collector，{@link #getOrRegisterFluid(Fluid)} 一次性把它们串起来并维护去重；跨 domain API 由
 * FluidDomainStore 包装后暴露。方块和容器的 item variant 由对应 collector 通过 item store 解析。
 */
public final class FluidDomainData {

    private final String datasetId;
    private final ForgeFluidIdentityResolver identityResolver = new ForgeFluidIdentityResolver();
    private final FluidDetailCollector detailCollector = new FluidDetailCollector();
    private final FluidBlockCollector blockCollector;
    private final FluidContainerCollector containerCollector;
    private final Map<String, FluidRow> fluids = new LinkedHashMap<>();
    private final Map<String, FluidContainerRow> containers = new LinkedHashMap<>();
    private final Map<String, FluidBlockRow> blocks = new LinkedHashMap<>();

    public FluidDomainData(String datasetId, ItemDomainStore itemStore) {
        this.datasetId = datasetId;
        this.blockCollector = new FluidBlockCollector(itemStore);
        this.containerCollector = new FluidContainerCollector(itemStore);
    }

    public FluidRow getOrRegisterFluid(Fluid fluid) {
        FluidIdentity identity = identityResolver.resolve(fluid);
        FluidRow existing = fluids.get(identity.getFluidId());
        if (existing != null) {
            return existing;
        }
        FluidRow row = detailCollector.collect(datasetId, identity, new FluidStack(fluid, 1));
        fluids.put(identity.getFluidId(), row);
        addBlock(row.getFluidId(), fluid);
        addContainers(row.getFluidId(), fluid);
        return row;
    }

    /**
     * 反向补充：把一个流体容器物品并入 fluid domain。
     *
     * <p>
     * 由配方等后续流程在遇到流体容器时调用。本方法自行解析流体、必要时补齐流体（连带方块和既定容器），再写入该容器关联；非流体容器忽略。
     */
    public void registerContainer(ItemStack containerStack) {
        Fluid fluid = containerCollector.resolveFluid(containerStack);
        if (fluid == null) {
            return;
        }
        FluidRow row = getOrRegisterFluid(fluid);
        putContainer(containerCollector.collectOne(datasetId, row.getFluidId(), containerStack));
    }

    private void addBlock(String fluidId, Fluid fluid) {
        FluidBlockRow row = blockCollector.collect(datasetId, fluidId, fluid);
        if (row != null) {
            blocks.putIfAbsent(fluidId + '\u0000' + row.getItemVariantId(), row);
        }
    }

    private void addContainers(String fluidId, Fluid fluid) {
        for (FluidContainerRow row : containerCollector.collect(datasetId, fluidId, fluid)) {
            putContainer(row);
        }
    }

    private void putContainer(FluidContainerRow row) {
        if (row != null) {
            containers.putIfAbsent(row.getFluidId() + '\u0000' + row.getItemVariantId(), row);
        }
    }

    public IExportModel toExportModel() {
        return new FluidExportModel(
            new ArrayList<>(fluids.values()),
            new ArrayList<>(containers.values()),
            new ArrayList<>(blocks.values()));
    }
}
