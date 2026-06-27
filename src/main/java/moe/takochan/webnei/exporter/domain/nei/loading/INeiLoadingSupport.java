package moe.takochan.webnei.exporter.domain.nei.loading;

import moe.takochan.webnei.exporter.domain.nei.scan.NeiHandlerEntry;

/**
 * 实验性 NEI 探测/抽取代码；当前未接入 ExportPlan.ALL 的正式导出流程。
 * 请勿在正式导出链路中引用，仅供参考。
 * NEI handler 加载支持接口。
 */
public interface INeiLoadingSupport {

    boolean supports(NeiHandlerEntry entry);

    NeiLoadingResult load(NeiHandlerEntry entry);
}
