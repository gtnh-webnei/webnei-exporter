package moe.takochan.webnei.exporter.nei.scan;

import codechicken.nei.recipe.IRecipeHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** NEI handler 扫描结果条目，绑定领域描述和原始运行时 handler 实例。 */
@Getter
@RequiredArgsConstructor
public final class NeiHandlerEntry {

    /** 可序列化的 handler 描述。 */
    private final NeiHandlerDescriptor descriptor;

    /** 运行时 NEI handler 实例，仅供后续加载 recipe 使用，不直接写出。 */
    private final IRecipeHandler handler;
}
