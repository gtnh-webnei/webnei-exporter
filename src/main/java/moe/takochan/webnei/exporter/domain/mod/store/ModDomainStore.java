package moe.takochan.webnei.exporter.domain.mod.store;

import moe.takochan.webnei.exporter.domain.mod.internal.ModDomainData;
import moe.takochan.webnei.exporter.domain.mod.internal.ModRegistrar;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/**
 * mod domain store — mod domain 的跨 domain 交互边界。
 *
 * <p>
 * 该类只暴露其他 domain 允许依赖的公开 API，内部列表和导出模型组装细节由 ModDomainData 维护。
 */
public final class ModDomainStore implements IDomainStore<ModDomainData, ModRegistrar> {

    private final ModDomainData data;
    private final ModRegistrar registrar;

    public ModDomainStore(ModDomainData data, ModRegistrar registrar) {
        this.data = data;
        this.registrar = registrar;
    }

    @Override
    public ModDomainData data() {
        return data;
    }

    @Override
    public ModRegistrar registrar() {
        return registrar;
    }
}
