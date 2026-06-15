package moe.takochan.webnei.exporter.domain.item;

import java.util.List;

import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.dataset.task.DatasetIdentity;
import moe.takochan.webnei.exporter.domain.item.model.ItemToolClassRow;

/** 采集 stack-sensitive 的工具类型和 harvest level，结果挂到 item_variant。 */
public interface IItemToolClassCollector {

    List<ItemToolClassRow> collect(DatasetIdentity dataset, ItemVariantIdentity variant, ItemStack stack);
}
