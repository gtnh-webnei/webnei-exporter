package moe.takochan.webnei.exporter.domain.asset.render;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import codechicken.nei.guihook.GuiContainerManager;
import moe.takochan.webnei.exporter.domain.asset.AssetContract;
import moe.takochan.webnei.exporter.domain.asset.internal.AssetPath;
import moe.takochan.webnei.exporter.domain.asset.render.client.DynamicTextureState;
import moe.takochan.webnei.exporter.domain.asset.render.client.FboIconRenderer;
import moe.takochan.webnei.exporter.domain.asset.render.hook.ITimeDriverHook;
import moe.takochan.webnei.exporter.domain.asset.render.hook.TimeDriverHookRegistry;

public final class ItemIconRenderer implements IAssetRenderer {

    private final FboIconRenderer fboRenderer = new FboIconRenderer();
    private final IconAnimator animator = new IconAnimator(fboRenderer);
    private final TimeDriverHookRegistry timeDrivers = new TimeDriverHookRegistry();
    private final boolean disableAnimations;

    public ItemIconRenderer() {
        this(false);
    }

    public ItemIconRenderer(boolean disableAnimations) {
        this.disableAnimations = disableAnimations;
    }

    @Override
    public boolean supports(AssetRenderJob job) {
        return isItemStackIcon(job) && job.getItemStack() != null;
    }

    @Override
    public IconTile prepareTile(final AssetRenderJob job) throws AssetRenderException {
        final ItemStack stack = job.getItemStack();
        if (!disableAnimations && (timeDrivers.find(job) != null || DynamicTextureState.from(stack)
            .isStandardAtlasAnimation())) {
            return null;
        }
        return new IconTile(
            job,
            relativePath(job),
            iconCanvasSize(stack),
            drawAction(stack),
            AssetRenderMetadata.staticImage());
    }

    @Override
    public RenderedAsset renderImage(AssetRenderJob job) throws AssetRenderException {
        ItemStack stack = job.getItemStack();
        if (disableAnimations) {
            int size = iconCanvasSize(stack);
            return RenderedAsset.png(
                job,
                relativePath(job),
                fboRenderer.render(size, drawAction(stack)),
                AssetRenderMetadata.staticImage());
        }
        DynamicTextureState dynamic = DynamicTextureState.from(stack);
        IconAnimator.RenderedIcon icon;
        // 标准 atlas 动画的物品（即使同时由 hook 命中）优先走 atlas 路径，避免时间驱动的固定 N 帧
        // 抢走原 atlas 帧序列导致底图丢失。仅对纯时间驱动（无 atlas 动画）走采样多帧。
        ITimeDriverHook hook = dynamic.isStandardAtlasAnimation() ? null : timeDrivers.find(job);
        if (hook != null) {
            icon = animator.renderTimeDriven(hook, iconCanvasSize(stack), drawAction(stack));
        } else {
            icon = animator.render(dynamic, iconCanvasSize(stack), drawAction(stack));
        }
        return RenderedAsset.png(job, relativePath(job), icon.getImage(), icon.getMetadataJson());
    }

    private static FboIconRenderer.IconRenderAction drawAction(final ItemStack stack) {
        return new FboIconRenderer.IconRenderAction() {

            @Override
            public void render() {
                GuiContainerManager.drawItem(0, 0, stack);
            }
        };
    }

    private static boolean isItemStackIcon(AssetRenderJob job) {
        return AssetContract.KIND_ITEM_ICON.equals(job.getKind())
            || AssetContract.KIND_RECIPE_CATEGORY_ICON.equals(job.getKind());
    }

    private static String relativePath(AssetRenderJob job) {
        if (AssetContract.KIND_RECIPE_CATEGORY_ICON.equals(job.getKind())) {
            return AssetPath.recipeCategoryIcon(job.getOwnerId());
        }
        return AssetPath.itemIcon(job.getOwnerId());
    }

    private static int iconCanvasSize(ItemStack stack) {
        IIcon icon = stack.getIconIndex();
        if (icon == null && stack.getItem() != null) {
            icon = stack.getItem()
                .getIcon(stack, 0);
        }
        int nativeSize = FboIconRenderer.DEFAULT_WEB_ICON_SIZE;
        if (icon != null) {
            nativeSize = Math.max(icon.getIconWidth(), icon.getIconHeight());
        }
        return Math.max(FboIconRenderer.DEFAULT_WEB_ICON_SIZE, nativeSize);
    }
}
