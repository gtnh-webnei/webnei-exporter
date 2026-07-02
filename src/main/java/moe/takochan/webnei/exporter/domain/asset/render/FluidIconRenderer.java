package moe.takochan.webnei.exporter.domain.asset.render;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;

import codechicken.nei.guihook.GuiContainerManager;
import gregtech.api.util.GTUtility;
import moe.takochan.webnei.exporter.domain.asset.AssetContract;
import moe.takochan.webnei.exporter.domain.asset.internal.AssetPath;
import moe.takochan.webnei.exporter.domain.asset.render.client.DynamicTextureState;
import moe.takochan.webnei.exporter.domain.asset.render.client.FboIconRenderer;
import moe.takochan.webnei.exporter.domain.asset.render.hook.ITimeDriverHook;
import moe.takochan.webnei.exporter.domain.asset.render.hook.TimeDriverHookRegistry;

/**
 * 流体图标渲染。
 *
 * <p>
 * 通过 {@link GTUtility#getFluidDisplayStack} 把流体包装成 GT 的 fluid display {@link ItemStack}，再走与物品图标
 * 相同的 {@link GuiContainerManager#drawItem} 路径渲染，使导出结果与游戏内 NEI 显示一致（含 GT 对特定流体的特殊
 * 渲染）。动画检测仍基于底层流体的 still sprite——display stack 的 fallback 渲染绘制的正是同一 sprite，逐帧推进
 * 与采样因此对齐。
 */
public final class FluidIconRenderer implements IAssetRenderer {

    private final FboIconRenderer fboRenderer = new FboIconRenderer();
    private final IconAnimator animator = new IconAnimator(fboRenderer);
    private final TimeDriverHookRegistry timeDrivers = new TimeDriverHookRegistry();
    private final boolean disableAnimations;

    public FluidIconRenderer() {
        this(false);
    }

    public FluidIconRenderer(boolean disableAnimations) {
        this.disableAnimations = disableAnimations;
    }

    @Override
    public boolean supports(AssetRenderJob job) {
        return AssetContract.KIND_FLUID_ICON.equals(job.getKind()) && job.getFluidStack() != null;
    }

    @Override
    public IconTile prepareTile(final AssetRenderJob job) throws AssetRenderException {
        final FluidStack stack = job.getFluidStack();
        // 时间驱动或 atlas 动画都不可批量（需逐帧推进时间或 sprite），退回 renderImage；
        // 但 disableAnimations 模式下强制走静态批量路径。
        if (!disableAnimations && (timeDrivers.find(job) != null
            || DynamicTextureState.fromIcon(stillIcon(stack), TextureMap.locationBlocksTexture)
                .isStandardAtlasAnimation())) {
            return null;
        }
        return new IconTile(
            job,
            AssetPath.fluidIcon(job.getOwnerId()),
            FboIconRenderer.DEFAULT_WEB_ICON_SIZE,
            drawAction(stack),
            AssetRenderMetadata.staticImage());
    }

    @Override
    public RenderedAsset renderImage(final AssetRenderJob job) throws AssetRenderException {
        FluidStack stack = job.getFluidStack();
        if (disableAnimations) {
            return RenderedAsset.png(
                job,
                AssetPath.fluidIcon(job.getOwnerId()),
                fboRenderer.render(FboIconRenderer.DEFAULT_WEB_ICON_SIZE, drawAction(stack)),
                AssetRenderMetadata.staticImage());
        }
        DynamicTextureState dynamic = DynamicTextureState.fromIcon(stillIcon(stack), TextureMap.locationBlocksTexture);
        IconAnimator.RenderedIcon rendered;
        // 标准 atlas 动画的流体（即使同时由 hook 命中）优先走 atlas 路径，避免时间驱动的固定 N 帧
        // 抢走原 atlas 帧序列导致底图丢失。仅对纯时间驱动（无 atlas 动画）走采样多帧。
        ITimeDriverHook hook = dynamic.isStandardAtlasAnimation() ? null : timeDrivers.find(job);
        if (hook != null) {
            rendered = animator.renderTimeDriven(hook, FboIconRenderer.DEFAULT_WEB_ICON_SIZE, drawAction(stack));
        } else {
            rendered = animator.render(dynamic, FboIconRenderer.DEFAULT_WEB_ICON_SIZE, drawAction(stack));
        }
        return RenderedAsset
            .png(job, AssetPath.fluidIcon(job.getOwnerId()), rendered.getImage(), rendered.getMetadataJson());
    }

    private static IIcon stillIcon(FluidStack stack) {
        return stack.getFluid()
            .getStillIcon();
    }

    private static FboIconRenderer.IconRenderAction drawAction(final FluidStack stack) {
        final ItemStack displayStack = GTUtility.getFluidDisplayStack(stack, false);
        return new FboIconRenderer.IconRenderAction() {

            @Override
            public void render() {
                GuiContainerManager.drawItem(0, 0, displayStack);
            }
        };
    }
}
