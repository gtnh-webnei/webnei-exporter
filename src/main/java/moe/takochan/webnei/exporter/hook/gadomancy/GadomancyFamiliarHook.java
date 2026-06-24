package moe.takochan.webnei.exporter.hook.gadomancy;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import makeo.gadomancy.client.renderers.item.ItemRenderFamiliar;
import makeo.gadomancy.client.util.FamiliarHandlerClient;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.asset.render.hook.AbstractPlayerTickHook;
import moe.takochan.webnei.exporter.domain.asset.render.hook.ITimeDriverSession;

/**
 * Gadomancy {@code ItemRenderFamiliar}：渲染时把
 * {@code FamiliarHandlerClient.PartialEntityFamiliar.DUMMY_FAMILIAR.ticksExisted} 拷给一个 EntityWisp，
 * EntityWisp 渲染依赖该 tick 流动产生 wisp 帧 / 尺寸变化。
 *
 * <p>
 * 时间源不是 player tick——是 dummy familiar 上的自定义 {@code ticksExisted} 字段，因此覆盖
 * {@link #begin} 返回内部 {@link FamiliarTickSession}。
 */
public final class GadomancyFamiliarHook extends AbstractPlayerTickHook {

    @Override
    public boolean isAvailable() {
        return Mods.GADOMANCY.isLoaded();
    }

    @Override
    protected boolean matchesItem(ItemStack stack) {
        IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.INVENTORY);
        return renderer instanceof ItemRenderFamiliar;
    }

    @Override
    public ITimeDriverSession begin() {
        FamiliarHandlerClient.PartialEntityFamiliar dummy = FamiliarHandlerClient.PartialEntityFamiliar.DUMMY_FAMILIAR;
        return dummy == null ? null : new FamiliarTickSession(dummy);
    }

    /** 推进 dummy familiar 的 {@code ticksExisted} public 字段；close 时复原。 */
    private static final class FamiliarTickSession implements ITimeDriverSession {

        private final FamiliarHandlerClient.PartialEntityFamiliar familiar;
        private final int baseline;

        FamiliarTickSession(FamiliarHandlerClient.PartialEntityFamiliar familiar) {
            this.familiar = familiar;
            this.baseline = familiar.ticksExisted;
        }

        @Override
        public void advanceTo(int step) {
            familiar.ticksExisted = baseline + step;
        }

        @Override
        public void close() {
            familiar.ticksExisted = baseline;
        }
    }
}
