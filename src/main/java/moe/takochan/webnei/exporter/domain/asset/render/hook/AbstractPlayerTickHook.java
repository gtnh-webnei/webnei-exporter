package moe.takochan.webnei.exporter.domain.asset.render.hook;

import net.minecraft.item.ItemStack;

import moe.takochan.webnei.exporter.domain.asset.AssetContract;
import moe.takochan.webnei.exporter.domain.asset.render.AssetRenderJob;

/**
 * 时间驱动 hook 的通用基类——目标渲染器读 player 实例的 {@code ticksExisted}（或可对齐 player tick 的字段）。
 * 子类只需实现 {@link #isAvailable} 与 {@link #matchesItem}。
 *
 * <p>
 * 默认采样窗口 60 帧、步长 1 tick；若特定 hook 需要不同周期，可覆盖 {@link #sampleCount} / {@link #tickStep}。
 * 默认 {@link #begin} 写 player.ticksExisted；写其他时间字段（如 dummy entity / 自定义 tick 字段）的 hook
 * 应覆盖 {@link #begin} 返回自有 {@link ITimeDriverSession} 实现。
 */
public abstract class AbstractPlayerTickHook implements ITimeDriverHook {

    private static final int DEFAULT_SAMPLE_COUNT = 60;
    private static final int DEFAULT_TICK_STEP = 1;

    @Override
    public int sampleCount() {
        return DEFAULT_SAMPLE_COUNT;
    }

    @Override
    public int tickStep() {
        return DEFAULT_TICK_STEP;
    }

    @Override
    public ITimeDriverSession begin() {
        return PlayerTickSession.beginIfReady();
    }

    @Override
    public boolean applies(AssetRenderJob job) {
        return job.getItemStack() != null && isItemKind(job) && matchesItem(job.getItemStack());
    }

    /** 子类判断该物品的 INVENTORY 渲染器是否被本 hook 覆盖。 */
    protected abstract boolean matchesItem(ItemStack stack);

    /** 仅物品/配方分类图标走 INVENTORY 路径，其他资产类型一律不命中。 */
    protected static boolean isItemKind(AssetRenderJob job) {
        return AssetContract.KIND_ITEM_ICON.equals(job.getKind())
            || AssetContract.KIND_RECIPE_CATEGORY_ICON.equals(job.getKind());
    }
}
