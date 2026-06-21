package moe.takochan.webnei.exporter.domain.asset.render;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import codechicken.nei.guihook.GuiContainerManager;
import moe.takochan.webnei.exporter.domain.asset.AssetContract;
import moe.takochan.webnei.exporter.domain.asset.internal.AssetPath;
import moe.takochan.webnei.exporter.domain.asset.render.client.DynamicTextureState;
import moe.takochan.webnei.exporter.domain.asset.render.client.FboIconRenderer;

public final class ItemIconRenderer implements IAssetRenderer {

    private final FboIconRenderer fboRenderer = new FboIconRenderer();

    @Override
    public boolean supports(AssetRenderJob job) {
        return isItemStackIcon(job) && job.getItemStack() != null;
    }

    @Override
    public RenderedAsset renderImage(AssetRenderJob job) throws AssetRenderException {
        RenderedIcon icon = renderIcon(job.getItemStack());
        return RenderedAsset.png(job, relativePath(job), icon.image, icon.metadataJson);
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

    private RenderedIcon renderIcon(ItemStack stack) throws AssetRenderException {
        DynamicTextureState dynamic = DynamicTextureState.from(stack);
        if (dynamic.isStandardAtlasAnimation()) {
            return renderAnimation(stack, dynamic);
        }
        return new RenderedIcon(renderFrame(stack), AssetRenderMetadata.staticImage());
    }

    private RenderedIcon renderAnimation(ItemStack stack, DynamicTextureState dynamic) throws AssetRenderException {
        BufferedImage[] frames = new BufferedImage[dynamic.getFrameCount()];
        dynamic.saveState();
        try {
            for (int i = 0; i < dynamic.getFrameCount(); i++) {
                int index = dynamic.currentIndex();
                if (index < 0 || index >= frames.length) {
                    throw new AssetRenderException("Invalid dynamic texture frame index: " + index);
                }
                frames[index] = renderFrame(stack);
                dynamic.updateAnimation();
            }
        } finally {
            dynamic.restoreState();
        }

        for (BufferedImage frame : frames) {
            if (frame == null) {
                return new RenderedIcon(renderFrame(stack), AssetRenderMetadata.staticImage());
            }
        }
        if (allFramesEqual(frames)) {
            return new RenderedIcon(frames[0], AssetRenderMetadata.staticImage());
        }
        BufferedImage spritesheet = concatenateHorizontal(frames);
        return new RenderedIcon(
            spritesheet,
            AssetRenderMetadata.horizontalExpandedTicks(frames[0].getWidth(), frames[0].getHeight(), frames.length));
    }

    private BufferedImage renderFrame(final ItemStack stack) throws AssetRenderException {
        return fboRenderer.render(iconCanvasSize(stack), new FboIconRenderer.IconRenderAction() {

            @Override
            public void render() {
                GuiContainerManager.drawItem(0, 0, stack);
            }
        });
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

    private static BufferedImage concatenateHorizontal(BufferedImage[] frames) {
        int height = 0;
        int width = 0;
        for (BufferedImage frame : frames) {
            width += frame.getWidth();
            height = Math.max(height, frame.getHeight());
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.Src);
        int x = 0;
        for (BufferedImage frame : frames) {
            graphics.drawImage(frame, x, (height - frame.getHeight()) / 2, null);
            x += frame.getWidth();
        }
        graphics.dispose();
        return image;
    }

    private static boolean allFramesEqual(BufferedImage[] frames) {
        if (frames.length <= 1) {
            return true;
        }
        BufferedImage first = frames[0];
        int width = first.getWidth();
        int height = first.getHeight();
        int[] firstPixels = first.getRGB(0, 0, width, height, null, 0, width);
        for (int i = 1; i < frames.length; i++) {
            BufferedImage frame = frames[i];
            if (frame.getWidth() != width || frame.getHeight() != height) {
                return false;
            }
            int[] pixels = frame.getRGB(0, 0, width, height, null, 0, width);
            if (!Arrays.equals(firstPixels, pixels)) {
                return false;
            }
        }
        return true;
    }

    private static final class RenderedIcon {

        private final BufferedImage image;
        private final String metadataJson;

        private RenderedIcon(BufferedImage image, String metadataJson) {
            this.image = image;
            this.metadataJson = metadataJson;
        }
    }
}
