package moe.takochan.webnei.exporter.domain.item.store;

import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.item.internal.ItemDomainData;
import moe.takochan.webnei.exporter.domain.item.internal.ItemRegistrar;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/**
 * item domain store — item domain 的跨 domain 交互边界。
 *
 * <p>
 * 其他 domain 只能通过本 store 解析或查询 item variant，不应直接依赖 item domain 的 model/internal 实现。
 * 具体 item/variant/toolclass 注册逻辑在 internal 中维护。
 */
public final class ItemDomainStore implements IDomainStore<ItemDomainData, ItemRegistrar> {

    private final ItemDomainData data;
    private final ItemRegistrar registrar;

    public ItemDomainStore(ItemDomainData data, ItemRegistrar registrar) {
        this.data = data;
        this.registrar = registrar;
    }

    /** Returns whether the stack can be persisted with a stable item identity. */
    public boolean hasStableIdentity(ItemStack stack) {
        return registrar.hasStableIdentity(stack);
    }

    @Override
    public ItemDomainData data() {
        return data;
    }

    @Override
    public ItemRegistrar registrar() {
        return registrar;
    }
}
