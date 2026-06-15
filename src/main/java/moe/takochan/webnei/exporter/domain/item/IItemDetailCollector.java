package moe.takochan.webnei.exporter.domain.item;

import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.dataset.task.DatasetIdentity;
import moe.takochan.webnei.exporter.domain.item.model.ItemRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;

/** 从真实 ItemStack 采集 item 基础字段和 item_variant 展示字段。 */
public interface IItemDetailCollector {

    ItemRow collectItem(DatasetIdentity dataset, ItemIdentity item, ItemStack stack);

    ItemVariantRow collectVariant(DatasetIdentity dataset, ItemVariantIdentity variant, ItemStack stack,
        String assetId);
}
