package moe.takochan.webnei.exporter.domain.item.hook;

import java.util.List;

import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.engine.hook.HookRegistry;

/** 持有所有可用的 item variant enrichment hook，由 ItemRegistrar 在注册 variant 时调用。 */
public final class ItemVariantHookRegistry {

    private final List<IItemVariantEnrichmentHook> hooks;

    /**
     * 创建 item variant hook registry。
     */
    public ItemVariantHookRegistry() {
        this.hooks = HookRegistry.get(IItemVariantEnrichmentHook.class);
    }

    /**
     * 依次调用所有已注册 hook 补充 row 字段。
     *
     * @param stack 当前正在注册的 ItemStack
     * @param row   基础字段已填充的 variant 行
     */
    public void enrich(ItemStack stack, ItemVariantRow row) {
        for (IItemVariantEnrichmentHook hook : hooks) {
            hook.enrich(stack, row);
        }
    }
}
