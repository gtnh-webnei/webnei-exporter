package moe.takochan.webnei.exporter.domain.dataset.internal;

public final class DatasetRegistrar {

    private final DatasetDomainData data;

    public DatasetRegistrar(DatasetDomainData data) {
        this.data = data;
    }

    public void register(String packSlug, String packVersion, String variant, String language) {
        data.register(packSlug, packVersion, variant, language);
    }
}
