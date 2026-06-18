package moe.takochan.webnei.exporter.domain.fluid.internal;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import moe.takochan.webnei.exporter.domain.fluid.model.FluidBlockRow;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;

/**
 * 采集流体在世界中的方块物品形态，产出 fluid_block 行。
 *
 * <p>
 * Forge 流体方块的 metadata 表示世界液位；物品侧用 meta=0、无 NBT 的默认 block stack。item variant 通过 item store 解析。
 */
public final class FluidBlockCollector {

    private final ItemDomainStore itemStore;

    public FluidBlockCollector(ItemDomainStore itemStore) {
        this.itemStore = itemStore;
    }

    /** 采集流体方块行；无对应方块时返回 {@code null}。 */
    public FluidBlockRow collect(String datasetId, String fluidId, Fluid fluid) {
        Block block = fluid.getBlock();
        if (block == null) {
            return null;
        }
        Item item = Item.getItemFromBlock(block);
        if (item == null) {
            return null;
        }
        String itemVariantId = itemStore.getOrRegisterVariant(new ItemStack(item, 1, 0))
            .getItemVariantId();
        return new FluidBlockRow(datasetId, fluidId, itemVariantId);
    }
}
