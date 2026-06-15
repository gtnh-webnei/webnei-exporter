package moe.takochan.webnei.exporter.nei.loading;

import lombok.Getter;

/** 通用 NEI handler 加载来源类型。 */
@Getter
public enum NeiLoadingSource {

    NONE(""),
    CORE_NEI_BUILTIN("core:nei-builtin"),
    MOD_ADAPTER("mod-adapter"),
    REGISTERED("registered");

    /** 写入诊断结果的来源标签。 */
    private final String label;

    NeiLoadingSource(String label) {
        this.label = label;
    }
}
