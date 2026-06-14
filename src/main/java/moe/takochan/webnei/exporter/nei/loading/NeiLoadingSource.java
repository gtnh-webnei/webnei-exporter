package moe.takochan.webnei.exporter.nei.loading;

public enum NeiLoadingSource {

    NONE(""),
    CORE_NEI_BUILTIN("core:nei-builtin"),
    MOD_ADAPTER("mod-adapter"),
    REGISTERED("registered");

    public final String label;

    NeiLoadingSource(String label) {
        this.label = label;
    }
}
