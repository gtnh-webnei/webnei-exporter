package moe.takochan.webnei.exporter.domain.asset;

import net.minecraft.item.ItemStack;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 延迟资源渲染请求。
 *
 * <p>
 * item 阶段只决定需要渲染哪个 ItemStack，并把 stack.copy() 带到请求里；真正渲染和 asset 表写出由后续 Asset task 完成。
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class AssetRequest {

    /** 资源归属对象类型，例如 item_variant。 */
    private final String ownerType;

    /** 资源归属对象 ID。 */
    private final String ownerId;

    /** 稳定资源 ID，也是后续 asset 表和文件路径的关联键。 */
    private final String assetId;

    /** 待渲染的 ItemStack 副本。 */
    private final ItemStack stack;
}
