package moe.takochan.webnei.exporter.domain.aspect.store;

import moe.takochan.webnei.exporter.domain.aspect.internal.AspectDomainData;
import moe.takochan.webnei.exporter.domain.aspect.internal.AspectRegistrar;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/** aspect domain 的跨 domain store 边界。 */
public final class AspectDomainStore implements IDomainStore<AspectDomainData, AspectRegistrar> {

    private final AspectDomainData data;
    private final AspectRegistrar registrar;

    public AspectDomainStore(AspectDomainData data, AspectRegistrar registrar) {
        this.data = data;
        this.registrar = registrar;
    }

    @Override
    public AspectDomainData data() {
        return data;
    }

    @Override
    public AspectRegistrar registrar() {
        return registrar;
    }
}
