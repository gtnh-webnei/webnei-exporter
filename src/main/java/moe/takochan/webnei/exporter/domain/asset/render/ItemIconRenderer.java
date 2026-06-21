package moe.takochan.webnei.exporter.domain.asset.render;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import codechicken.nei.guihook.GuiContainerManager;
import moe.takochan.webnei.exporter.domain.asset.AssetContract;
import moe.takochan.webnei.exporter.domain.asset.internal.AssetPath;
import moe.takochan.webnei.exporter.domain.asset.render.client.DynamicTextureState;
import moe.takochan.webnei.exporter.domain.asset.render.client.FboIconRenderer;

public final class ItemIconRenderer implements IAssetRenderer {

    private final FboIconRenderer fboRenderer = new FboIconRenderer();
    private final IconAnimator animator = new IconAnimator(fboRenderer);

    @Override
    public boolean supports(AssetRenderJob job) {
        return isItemStackIcon(job) && job.getItemStack() != null;
    }

    @Override
    public IconTile prepareTile(final AssetRenderJob job) throws AssetRenderException {
        final ItemStack stack = job.getItemStack();
        if (DynamicTextureState.from(stack)
            .isStandardAtlasAnimation()) {
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
        IconAnimator.RenderedIcon icon = animator
            .render(DynamicTextureState.from(stack), iconCanvasSize(stack), drawAction(stack));
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
