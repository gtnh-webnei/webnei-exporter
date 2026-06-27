package moe.takochan.webnei.exporter.domain.nei.loading;

/**
 * 实验性 NEI 探测/抽取代码；当前未接入 ExportPlan.ALL 的正式导出流程。
 * 请勿在正式导出链路中引用，仅供参考。
 * NEI handler 加载状态。
 */
public enum NeiLoadingStatus {
    LOADED,
    UNSUPPORTED,
    ERROR
}
