package moe.takochan.webnei.exporter.domain.asset.render;

import java.awt.image.BufferedImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import moe.takochan.webnei.exporter.domain.asset.AssetContract;
import moe.takochan.webnei.exporter.domain.asset.internal.AssetPath;
import moe.takochan.webnei.exporter.domain.asset.render.client.FboIconRenderer;

public final class FluidIconRenderer implements IAssetRenderer {

    private final FboIconRenderer fboRenderer = new FboIconRenderer();

    @Override
    public boolean supports(AssetRenderJob job) {
        return AssetContract.KIND_FLUID_ICON.equals(job.getKind()) && job.getFluidStack() != null;
    }

    @Override
    public RenderedAsset renderImage(final AssetRenderJob job) throws AssetRenderException {
        BufferedImage image = renderFluid(job.getFluidStack());
        return RenderedAsset.png(job, AssetPath.fluidIcon(job.getOwnerId()), image, AssetRenderMetadata.staticImage());
    }

    private BufferedImage renderFluid(final FluidStack stack) throws AssetRenderException {
        final IIcon icon = stack.getFluid()
            .getIcon(stack);
        if (icon == null) {
            throw new AssetRenderException(
                "Fluid has no icon: " + stack.getFluid()
                    .getName());
        }
        return fboRenderer.render(FboIconRenderer.DEFAULT_WEB_ICON_SIZE, new FboIconRenderer.IconRenderAction() {

            @Override
            public void render() {
                renderFluidIcon(stack, icon);
            }
        });
    }

    private static void renderFluidIcon(FluidStack stack, IIcon icon) {
        int color = stack.getFluid()
            .getColor(stack);
        float red = (float) (color >> 16 & FboIconRenderer.MAX_COLOR) / (float) FboIconRenderer.MAX_COLOR;
        float green = (float) (color >> 8 & FboIconRenderer.MAX_COLOR) / (float) FboIconRenderer.MAX_COLOR;
        float blue = (float) (color & FboIconRenderer.MAX_COLOR) / (float) FboIconRenderer.MAX_COLOR;

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(red, green, blue, 1.0F);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0.0D, FboIconRenderer.GUI_ICON_SIZE, 0.0D, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(
            FboIconRenderer.GUI_ICON_SIZE,
            FboIconRenderer.GUI_ICON_SIZE,
            0.0D,
            icon.getMaxU(),
            icon.getMaxV());
        tessellator.addVertexWithUV(FboIconRenderer.GUI_ICON_SIZE, 0.0D, 0.0D, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, icon.getMinU(), icon.getMinV());
        tessellator.draw();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

}
