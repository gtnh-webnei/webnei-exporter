package moe.takochan.webnei.exporter.domain.item;

/**
 * item panel 展示源接口。
 *
 * <p>
 * 实现只负责把展示列表映射到 ItemStackCatalog 和 panel entry，不拥有 item/item_variant 的身份规则。
 */
public interface IItemPanelSource {

    void collect(ItemStackCatalog catalog);
}
