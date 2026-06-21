package moe.takochan.webnei.exporter.domain.asset.render.client;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

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

    private Framebuffer framebuffer;
    private int framebufferSize;

    public BufferedImage render(int size, IconRenderAction action) throws AssetRenderException {
        ensureFramebuffer(size);
        RenderState state = RenderState.capture();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        try {
            return renderDualBackground(size, action);
        } finally {
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();
            state.restore();
        }
    }

    /**
     * 单 FBO 双区渲染：左半白底、右半黑底，各渲一次同一图标，再一次性读回整块像素。
     *
     * <p>
     * 与逐 pass 各读一次相比，{@code glReadPixels} 同步停顿从 2 次降为 1 次，alpha 仍由白/黑两个
     * 已知底色反推（见 {@link #extractPixel}），结果与逐 pass 等价。
     */
    private BufferedImage renderDualBackground(int size, IconRenderAction action) throws AssetRenderException {
        try {
            framebuffer.bindFramebuffer(true);
            GL11.glClearDepth(1.0D);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(0, 0, size, size);
            GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glScissor(size, 0, size, size);
            GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);

            applyGuiRenderState(size, 0);
            action.render();
            applyGuiRenderState(size, size);
            action.render();

            GL11.glFlush();
            return readAndExtract(size);
        } catch (RuntimeException e) {
            throw new AssetRenderException("Unable to render icon framebuffer", e);
        }
    }

    private static void applyGuiRenderState(int size, int viewportX) {
        GL11.glViewport(viewportX, 0, size, size);
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

    private void ensureFramebuffer(int size) {
        if (framebuffer == null || framebufferSize != size) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            // 宽度为 2*size：左半白底、右半黑底，单次渲染两套底色。
            framebuffer = new Framebuffer(size * 2, size, true);
            framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 1.0F);
            framebufferSize = size;
        }
    }

    /**
     * 一次性读回 2*size×size 整块像素，并在 int[] 上直接反推 alpha 输出图标。
     *
     * <p>
     * 左半为白底渲染结果、右半为黑底渲染结果。同时完成垂直翻转（glReadPixels 原点在左下）与
     * 白/黑反推，避免逐像素 {@code getRGB}/{@code setRGB}。BGRA 字节按小端读成 int 即为 ARGB 布局，
     * 与 {@link #extractPixel} 的位运算一致。
     */
    private static BufferedImage readAndExtract(int size) {
        int width = size * 2;
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * size * 4);
        GL11.glReadPixels(0, 0, width, size, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, buffer);
        int[] raw = new int[width * size];
        buffer.asIntBuffer()
            .get(raw);

        int[] out = new int[size * size];
        for (int y = 0; y < size; y++) {
            int sourceRow = (size - y - 1) * width;
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
