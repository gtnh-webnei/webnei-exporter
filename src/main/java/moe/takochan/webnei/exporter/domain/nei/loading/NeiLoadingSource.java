package moe.takochan.webnei.exporter.domain.nei.loading;

import lombok.Getter;

/**
 * 实验性 NEI 探测/抽取代码；当前未接入 ExportPlan.ALL 的正式导出流程。
 * 请勿在正式导出链路中引用，仅供参考。
 * 通用 NEI handler 加载来源类型。
 */
@Getter
public enum NeiLoadingSource {

    NONE(""),
    CORE_NEI_BUILTIN("core:nei-builtin"),
    REGISTERED("registered");

    /** 写入诊断结果的来源标签。 */
    private final String label;

    NeiLoadingSource(String label) {
        this.label = label;
    }
}
