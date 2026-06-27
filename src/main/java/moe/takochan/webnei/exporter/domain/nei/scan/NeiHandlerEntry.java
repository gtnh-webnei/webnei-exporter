package moe.takochan.webnei.exporter.domain.nei.scan;

import codechicken.nei.recipe.IRecipeHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 实验性 NEI 探测/抽取代码；当前未接入 ExportPlan.ALL 的正式导出流程。
 * 请勿在正式导出链路中引用，仅供参考。
 * NEI handler 扫描结果条目，绑定领域描述和原始运行时 handler 实例。
 */
@Getter
@RequiredArgsConstructor
public final class NeiHandlerEntry {

    /** 可序列化的 handler 描述。 */
    private final NeiHandlerDescriptor descriptor;

    /** 运行时 NEI handler 实例，仅供后续加载 recipe 使用，不直接写出。 */
    private final IRecipeHandler handler;
}
