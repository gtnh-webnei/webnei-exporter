package moe.takochan.webnei.exporter.domain.item.hook;

import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.engine.hook.IExportHook;

/**
 * item variant 字段补充扩展点。
 *
 * <p>
 * 在 item store 完成基础字段采集后、写入 variant 前被调用，用于从 mod 特有 API 提取额外字段写入 ItemVariantRow。
 */
public interface IItemVariantEnrichmentHook extends IExportHook {

    /**
     * 从 stack 中提取额外信息并写入 row 的可选字段。
     *
     * @param stack 当前正在注册的 ItemStack（已 copy）
     * @param row   基础字段已填充的 variant 行，可直接修改
     */
    void enrich(ItemStack stack, ItemVariantRow row);
}
