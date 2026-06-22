package moe.takochan.webnei.exporter.domain.fluid.store;

import moe.takochan.webnei.exporter.domain.fluid.internal.FluidDomainData;
import moe.takochan.webnei.exporter.domain.fluid.internal.FluidRegistrar;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/**
 * fluid domain store — fluid domain 的跨 domain 交互边界。
 *
 * <p>
 * 其他 domain 只能通过本 store 注册或查询流体，不应直接依赖 fluid domain 的 model/internal 实现。fluid/block/container 的注册和去重逻辑
 * 在 internal 中维护：注册流体时自动挂接其方块和容器关系。
 */
public final class FluidDomainStore implements IDomainStore<FluidDomainData, FluidRegistrar> {

    private final FluidDomainData data;
    private final FluidRegistrar registrar;

    public FluidDomainStore(FluidDomainData data, FluidRegistrar registrar) {
        this.data = data;
        this.registrar = registrar;
    }

    @Override
    public FluidDomainData data() {
        return data;
    }

    @Override
    public FluidRegistrar registrar() {
        return registrar;
    }
}
