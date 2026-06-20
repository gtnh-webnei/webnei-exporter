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
            BufferedImage white = renderPass(size, 1.0F, 1.0F, 1.0F, action);
            BufferedImage black = renderPass(size, 0.0F, 0.0F, 0.0F, action);
            return extractAlpha(white, black);
        } finally {
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();
            state.restore();
        }
    }

    private BufferedImage renderPass(int size, float red, float green, float blue, IconRenderAction action)
        throws AssetRenderException {
        try {
            framebuffer.bindFramebuffer(true);
            applyGuiRenderState(size);
            GL11.glClearColor(red, green, blue, 1.0F);
            GL11.glClearDepth(1.0D);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            action.render();
            GL11.glFlush();
            return readPixels(size);
        } catch (RuntimeException e) {
            throw new AssetRenderException("Unable to render icon framebuffer", e);
        }
    }

    private static void applyGuiRenderState(int size) {
        GL11.glViewport(0, 0, size, size);
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
            framebuffer = new Framebuffer(size, size, true);
            framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 1.0F);
            framebufferSize = size;
        }
    }

    private static BufferedImage readPixels(int size) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(size * size * 4);
        GL11.glReadPixels(0, 0, size, size, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, buffer);
        int[] pixels = new int[size * size];
        buffer.asIntBuffer()
            .get(pixels);

        int[] flipped = new int[pixels.length];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                flipped[x + y * size] = pixels[x + (size - y - 1) * size];
            }
        }

        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, size, size, flipped, 0, size);
        return image;
    }

    private static BufferedImage extractAlpha(BufferedImage white, BufferedImage black) {
        int width = white.getWidth();
        int height = white.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int whiteRgb = white.getRGB(x, y);
                int blackRgb = black.getRGB(x, y);
                image.setRGB(x, y, extractPixel(whiteRgb, blackRgb));
            }
        }
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

        private RenderState(IntBuffer viewport, int framebuffer, int texture, int matrixMode, boolean texture2d,
            boolean lighting, boolean depth, boolean blend, boolean alpha, boolean rescaleNormal, boolean scissor,
            boolean depthMask) {
            this.viewport = viewport;
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
            return new RenderState(
                viewport,
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
