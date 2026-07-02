package moe.takochan.webnei.exporter.domain.fluid.store;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * fluid 域对外解析"NEI 流体显示物"的结果：一个稳定 fluid_id 和该显示物携带的流体量。
 *
 * <p>
 * 供 recipe 等域把 NEI 配方页面里的"流体占位 ItemStack"转换为流体候选时使用；不暴露具体 NEI/Forge 类型。
 */
@Getter
@RequiredArgsConstructor
public final class FluidDisplayResolution {

    private final String fluidId;
    private final int amount;
}
