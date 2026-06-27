package moe.takochan.webnei.exporter.domain.nei.recipe;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moe.takochan.webnei.exporter.domain.nei.scan.NeiHandlerDescriptor;

/**
 * 实验性 NEI 探测/抽取代码；当前未接入 ExportPlan.ALL 的正式导出流程。
 * 请勿在正式导出链路中引用，仅供参考。
 * 一个 NEI handler 的 recipe 加载和抽取结果摘要。
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ExtractedHandler {

    /** 扫描阶段得到的 handler 静态描述。 */
    private final NeiHandlerDescriptor descriptor;

    /** 成功加载后的 recipe 数量；错误时为 -1。 */
    private final int recipeCount;

    /** 抽取状态，例如 core、generic、error、unsupported_loading。 */
    private final String status;

    /** 状态补充说明或错误原因。 */
    private final String reason;
}
