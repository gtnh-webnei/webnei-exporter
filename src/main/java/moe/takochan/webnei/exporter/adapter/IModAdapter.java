package moe.takochan.webnei.exporter.adapter;

import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.domain.nei.scan.NeiHandlerEntry;

/**
 * mod 级适配器接口。
 *
 * <p>
 * adapter 不拥有导出表结构，只在对应阶段补充语义字段或可靠 loading 能力。item 阶段通过 fillItemVariant 补充
 * chemical_expression 等允许字段。
 */
public interface IModAdapter {

    String id();

    boolean isAvailable();

    boolean supportsNeiHandler(NeiHandlerEntry entry);

    AdapterResult extractNeiHandler(NeiHandlerEntry entry, AdapterContext context);

    default void fillItemVariant(ItemStack stack, ItemVariantRow row, AdapterContext context) {}
}
