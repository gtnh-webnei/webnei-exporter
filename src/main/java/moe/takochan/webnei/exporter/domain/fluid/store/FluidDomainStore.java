package moe.takochan.webnei.exporter.domain.fluid.store;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.fluid.internal.FluidDomainData;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidRow;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/**
 * fluid domain store — fluid domain 的跨 domain 交互边界。
 *
 * <p>
 * 其他 domain 只能通过本 store 注册或查询流体，不应直接依赖 fluid domain 的 model/internal 实现。fluid/block/container 的注册和去重逻辑
 * 在 internal 中维护：注册流体时自动挂接其方块和容器关系。
 */
public final class FluidDomainStore implements IDomainStore {

    private final FluidDomainData data;

    public FluidDomainStore(FluidDomainData data) {
        this.data = data;
    }

    /** 获取 fluid 对应行；不存在时补齐 fluid 基础字段并挂接其方块、容器关系。 */
    public FluidRow getOrRegisterFluid(Fluid fluid) {
        return data.getOrRegisterFluid(fluid);
    }

    /** 返回已注册 fluid 对应的代表 FluidStack，供后续图标渲染使用。 */
    public Map<String, FluidStack> stacks() {
        return data.stacks();
    }

    /**
     * 反向补充：把一个流体容器物品并入 fluid domain。
     *
     * <p>
     * 供配方等后续流程在遇到流体容器时调用——只需把原始容器 stack 交过来，由 fluid domain 自行解析流体、必要时补齐流体并写入容器关联；
     * 非流体容器忽略。
     */
    public void registerContainer(ItemStack containerStack) {
        data.registerContainer(containerStack);
    }

    @Override
    public IExportModel toExportModel() {
        return data.toExportModel();
    }
}
