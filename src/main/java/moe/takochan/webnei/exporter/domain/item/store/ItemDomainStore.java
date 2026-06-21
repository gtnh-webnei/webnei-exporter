package moe.takochan.webnei.exporter.domain.item.store;

import java.util.Map;

import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.item.internal.ItemDomainData;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/**
 * item domain store — item domain 的跨 domain 交互边界。
 *
 * <p>
 * 其他 domain 只能通过本 store 解析或查询 item variant，不应直接依赖 item domain 的 model/internal 实现。
 * 具体 item/variant/toolclass 注册逻辑在 internal 中维护。
 */
public final class ItemDomainStore implements IDomainStore {

    private final ItemDomainData data;

    public ItemDomainStore(ItemDomainData data) {
        this.data = data;
    }

    /**
     * 获取 stack 对应 variant；不存在时补齐 item、variant 和 toolclass。
     */
    public ItemVariantRow getOrRegisterVariant(ItemStack input) {
        return data.getOrRegisterVariant(input);
    }

    /**
     * 返回已注册 variant 对应的原始 ItemStack。
     */
    public Map<String, ItemStack> stacks() {
        return data.stacks();
    }

    @Override
    public IExportModel toExportModel() {
        return data.toExportModel();
    }
}
