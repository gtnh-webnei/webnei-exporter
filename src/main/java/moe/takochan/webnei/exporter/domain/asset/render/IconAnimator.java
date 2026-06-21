package moe.takochan.webnei.exporter.domain.asset.render;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import moe.takochan.webnei.exporter.domain.asset.render.client.DynamicTextureState;
import moe.takochan.webnei.exporter.domain.asset.render.client.FboIconRenderer;

/**
 * 标准 atlas 动画图标的逐帧渲染与拼合。
 *
 * <p>
 * 对每个动画周期帧推进 atlas sprite、用 {@code action} 渲染一帧，再水平拼成 spritesheet。若帧不足、
 * 不可用或全部相同则退化为单帧静态图。物品与流体共用同一套逻辑，仅 {@code action} 与画布尺寸不同。
 */
final class IconAnimator {

    private final FboIconRenderer fboRenderer;

    IconAnimator(FboIconRenderer fboRenderer) {
        this.fboRenderer = fboRenderer;
    }

    /** 渲染图标：动画则返回 spritesheet + 动画 metadata，否则返回单帧 + 静态 metadata。 */
    RenderedIcon render(DynamicTextureState dynamic, int canvasSize, FboIconRenderer.IconRenderAction action)
        throws AssetRenderException {
        if (!dynamic.isStandardAtlasAnimation()) {
            return new RenderedIcon(fboRenderer.render(canvasSize, action), AssetRenderMetadata.staticImage());
        }
        return renderAnimation(dynamic, canvasSize, action);
    }

    private RenderedIcon renderAnimation(DynamicTextureState dynamic, int canvasSize,
        FboIconRenderer.IconRenderAction action) throws AssetRenderException {
        BufferedImage[] frames = new BufferedImage[dynamic.getFrameCount()];
        dynamic.saveState();
        try {
            for (int i = 0; i < dynamic.getFrameCount(); i++) {
                int index = dynamic.currentIndex();
                if (index < 0 || index >= frames.length) {
                    throw new AssetRenderException("Invalid dynamic texture frame index: " + index);
                }
                frames[index] = fboRenderer.render(canvasSize, action);
                dynamic.updateAnimation();
            }
        } finally {
            dynamic.restoreState();
        }

        for (BufferedImage frame : frames) {
            if (frame == null) {
                return new RenderedIcon(fboRenderer.render(canvasSize, action), AssetRenderMetadata.staticImage());
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

    /** 渲染产物：图像 + 元信息 JSON。 */
    static final class RenderedIcon {

        private final BufferedImage image;
        private final String metadataJson;

        RenderedIcon(BufferedImage image, String metadataJson) {
            this.image = image;
            this.metadataJson = metadataJson;
        }

        BufferedImage getImage() {
            return image;
        }

        String getMetadataJson() {
            return metadataJson;
        }
    }
}
