package moe.takochan.webnei.exporter.domain.asset.store;

import moe.takochan.webnei.exporter.domain.asset.internal.AssetDomainData;
import moe.takochan.webnei.exporter.domain.asset.internal.AssetRegistrar;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

public final class AssetDomainStore implements IDomainStore<AssetDomainData, AssetRegistrar> {

    private final AssetDomainData data;
    private final AssetRegistrar registrar;

    public AssetDomainStore(AssetDomainData data, AssetRegistrar registrar) {
        this.data = data;
        this.registrar = registrar;
    }

    @Override
    public AssetDomainData data() {
        return data;
    }

    @Override
    public AssetRegistrar registrar() {
        return registrar;
    }
}
