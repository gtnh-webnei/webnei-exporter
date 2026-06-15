package moe.takochan.webnei.exporter.domain.item;

/**
 * ItemStack 种子来源接口。
 *
 * <p>
 * 来源可以来自 Forge registry、NEI、recipe、quest 等；实现应把真实 ItemStack 交给 ItemStackCatalog.register，
 * 不直接生成表行。
 */
public interface IItemTypeSource {

    void collect(ItemStackCatalog catalog);
}
