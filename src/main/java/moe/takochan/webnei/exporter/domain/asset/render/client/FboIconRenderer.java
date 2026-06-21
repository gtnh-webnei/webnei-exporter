package moe.takochan.webnei.exporter.domain.asset.render.client;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import moe.takochan.webnei.exporter.domain.asset.render.AssetRenderException;

public final class FboIconRenderer {

    public static final int DEFAULT_WEB_ICON_SIZE = 64;
    public static final int GUI_ICON_SIZE = 16;
    public static final int MAX_COLOR = 255;

    /** atlas 单边最大像素，限制单次 readback 的显存/内存与 GPU 纹理尺寸上限。 */
    private static final int ATLAS_MAX_DIM = 2048;

    private Framebuffer framebuffer;
    private int framebufferWidth;
    private int framebufferHeight;

    /** 渲染单个图标，等价于一个元素的批次。 */
    public BufferedImage render(int size, IconRenderAction action) throws AssetRenderException {
        return renderBatch(size, Collections.singletonList(action)).get(0);
    }

    /** 给定图标尺寸，单张 atlas 能容纳的图标数（即一次 readback 的最大批量）。 */
    public int batchCapacity(int size) {
        int columns = Math.max(1, ATLAS_MAX_DIM / (size * 2));
        int rowsCap = Math.max(1, ATLAS_MAX_DIM / size);
        return columns * rowsCap;
    }

    /**
     * 批量渲染同一尺寸的一组图标。
     *
     * <p>
     * 将每个图标的白底/黑底两区铺进一张网格 atlas（单元为 2*size×size），整批渲染后对整张 atlas 做
     * 一次 {@code glReadPixels}，再逐单元在 int[] 上反推 alpha。相比逐图标各读一次，GPU→CPU 同步停顿
     * 从 N 次降到约 N/批 次。超过单张 atlas 容量时按 {@code capacity} 自动分块，每块一次读回。
     */
    public List<BufferedImage> renderBatch(int size, List<IconRenderAction> actions) throws AssetRenderException {
        List<BufferedImage> out = new ArrayList<>(actions.size());
        if (actions.isEmpty()) {
            return out;
        }
        int columns = Math.max(1, ATLAS_MAX_DIM / (size * 2));
        int rowsCap = Math.max(1, ATLAS_MAX_DIM / size);
        int capacity = columns * rowsCap;

        RenderState state = RenderState.capture();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        try {
            for (int start = 0; start < actions.size(); start += capacity) {
                int end = Math.min(actions.size(), start + capacity);
                renderChunk(size, columns, actions.subList(start, end), out);
            }
        } finally {
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();
            state.restore();
        }
        return out;
    }

    /** 渲染一块（不超过 atlas 容量）的图标，整块一次读回。 */
    private void renderChunk(int size, int columns, List<IconRenderAction> actions, List<BufferedImage> out)
        throws AssetRenderException {
        try {
            int count = actions.size();
            int rows = (count + columns - 1) / columns;
            int usedColumns = Math.min(count, columns);
            int atlasWidth = usedColumns * size * 2;
            int atlasHeight = rows * size;
            ensureFramebuffer(atlasWidth, atlasHeight);
            framebuffer.bindFramebuffer(true);

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glClearDepth(1.0D);
            GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            for (int i = 0; i < count; i++) {
                int col = i % columns;
                int row = i / columns;
                GL11.glScissor(col * size * 2 + size, row * size, size, size);
                GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            }
            GL11.glDisable(GL11.GL_SCISSOR_TEST);

            for (int i = 0; i < count; i++) {
                int baseX = (i % columns) * size * 2;
                int baseY = (i / columns) * size;
                IconRenderAction action = actions.get(i);
                applyGuiRenderState(size, baseX, baseY);
                action.render();
                applyGuiRenderState(size, baseX + size, baseY);
                action.render();
            }
            GL11.glFlush();

            ByteBuffer buffer = BufferUtils.createByteBuffer(atlasWidth * atlasHeight * 4);
            GL11.glReadPixels(0, 0, atlasWidth, atlasHeight, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, buffer);
            int[] raw = new int[atlasWidth * atlasHeight];
            buffer.asIntBuffer()
                .get(raw);

            for (int i = 0; i < count; i++) {
                int baseX = (i % columns) * size * 2;
                int baseY = (i / columns) * size;
                out.add(extractTile(raw, atlasWidth, baseX, baseY, size));
            }
        } catch (RuntimeException e) {
            throw new AssetRenderException("Unable to render icon batch", e);
        }
    }

    private static void applyGuiRenderState(int size, int viewportX, int viewportY) {
        GL11.glViewport(viewportX, viewportY, size, size);
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, GUI_ICON_SIZE, GUI_ICON_SIZE, 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }

    private void ensureFramebuffer(int width, int height) {
        if (framebuffer == null || framebufferWidth != width || framebufferHeight != height) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            framebuffer = new Framebuffer(width, height, true);
            framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 1.0F);
            framebufferWidth = width;
            framebufferHeight = height;
        }
    }

    /**
     * 从整块 atlas 的 int[] 中切出一个图标单元并反推 alpha。
     *
     * <p>
     * 单元左下角在 atlas 内为 ({@code baseX}, {@code baseY})，左半白底、右半黑底。完成垂直翻转
     * （glReadPixels 原点在左下）与白/黑反推（见 {@link #extractPixel}），避免逐像素 getRGB/setRGB。
     */
    private static BufferedImage extractTile(int[] raw, int atlasWidth, int baseX, int baseY, int size) {
        int[] out = new int[size * size];
        for (int y = 0; y < size; y++) {
            int sourceRow = (baseY + size - y - 1) * atlasWidth + baseX;
            int targetRow = y * size;
            for (int x = 0; x < size; x++) {
                int whiteRgb = raw[sourceRow + x];
                int blackRgb = raw[sourceRow + size + x];
                out[targetRow + x] = extractPixel(whiteRgb, blackRgb);
            }
        }
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, size, size, out, 0, size);
        return image;
    }

    private static int extractPixel(int whiteRgb, int blackRgb) {
        int whiteRed = whiteRgb >> 16 & MAX_COLOR;
        int whiteGreen = whiteRgb >> 8 & MAX_COLOR;
        int whiteBlue = whiteRgb & MAX_COLOR;
        int blackRed = blackRgb >> 16 & MAX_COLOR;
        int blackGreen = blackRgb >> 8 & MAX_COLOR;
        int blackBlue = blackRgb & MAX_COLOR;

        float alphaRed = 1.0F - (whiteRed - blackRed) / (float) MAX_COLOR;
        float alphaGreen = 1.0F - (whiteGreen - blackGreen) / (float) MAX_COLOR;
        float alphaBlue = 1.0F - (whiteBlue - blackBlue) / (float) MAX_COLOR;
        float alpha = clamp((alphaRed + alphaGreen + alphaBlue) / 3.0F, 0.0F, 1.0F);
        int outAlpha = Math.round(alpha * MAX_COLOR);
        if (alpha <= 0.01F) {
            return 0;
        }

        int outRed = clampColor(Math.round(blackRed / alpha));
        int outGreen = clampColor(Math.round(blackGreen / alpha));
        int outBlue = clampColor(Math.round(blackBlue / alpha));
        return outAlpha << 24 | outRed << 16 | outGreen << 8 | outBlue;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int clampColor(int value) {
        return Math.max(0, Math.min(MAX_COLOR, value));
    }

    public interface IconRenderAction {

        void render();
    }

    private static final class RenderState {

        private final IntBuffer viewport;
        private final IntBuffer scissorBox;
        private final int framebuffer;
        private final int texture;
        private final int matrixMode;
        private final boolean texture2d;
        private final boolean lighting;
        private final boolean depth;
        private final boolean blend;
        private final boolean alpha;
        private final boolean rescaleNormal;
        private final boolean scissor;
        private final boolean depthMask;

        private RenderState(IntBuffer viewport, IntBuffer scissorBox, int framebuffer, int texture, int matrixMode,
            boolean texture2d, boolean lighting, boolean depth, boolean blend, boolean alpha, boolean rescaleNormal,
            boolean scissor, boolean depthMask) {
            this.viewport = viewport;
            this.scissorBox = scissorBox;
            this.framebuffer = framebuffer;
            this.texture = texture;
            this.matrixMode = matrixMode;
            this.texture2d = texture2d;
            this.lighting = lighting;
            this.depth = depth;
            this.blend = blend;
            this.alpha = alpha;
            this.rescaleNormal = rescaleNormal;
            this.scissor = scissor;
            this.depthMask = depthMask;
        }

        private static RenderState capture() {
            IntBuffer viewport = BufferUtils.createIntBuffer(16);
            GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
            IntBuffer scissorBox = BufferUtils.createIntBuffer(16);
            GL11.glGetInteger(GL11.GL_SCISSOR_BOX, scissorBox);
            return new RenderState(
                viewport,
                scissorBox,
                GL11.glGetInteger(EXTFramebufferObject.GL_FRAMEBUFFER_BINDING_EXT),
                GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D),
                GL11.glGetInteger(GL11.GL_MATRIX_MODE),
                GL11.glIsEnabled(GL11.GL_TEXTURE_2D),
                GL11.glIsEnabled(GL11.GL_LIGHTING),
                GL11.glIsEnabled(GL11.GL_DEPTH_TEST),
                GL11.glIsEnabled(GL11.GL_BLEND),
                GL11.glIsEnabled(GL11.GL_ALPHA_TEST),
                GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL),
                GL11.glIsEnabled(GL11.GL_SCISSOR_TEST),
                GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK));
        }

        private void restore() {
            setEnabled(GL11.GL_TEXTURE_2D, texture2d);
            setEnabled(GL11.GL_LIGHTING, lighting);
            setEnabled(GL11.GL_DEPTH_TEST, depth);
            setEnabled(GL11.GL_BLEND, blend);
            setEnabled(GL11.GL_ALPHA_TEST, alpha);
            setEnabled(GL12.GL_RESCALE_NORMAL, rescaleNormal);
            setEnabled(GL11.GL_SCISSOR_TEST, scissor);
            GL11.glScissor(scissorBox.get(0), scissorBox.get(1), scissorBox.get(2), scissorBox.get(3));
            GL11.glDepthMask(depthMask);
            GL11.glColorMask(true, true, true, true);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, framebuffer);
            GL11.glViewport(viewport.get(0), viewport.get(1), viewport.get(2), viewport.get(3));
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
            GL11.glMatrixMode(matrixMode);
        }

        private static void setEnabled(int capability, boolean enabled) {
            if (enabled) {
                GL11.glEnable(capability);
            } else {
                GL11.glDisable(capability);
            }
        }
    }
}
