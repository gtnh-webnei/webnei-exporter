package moe.takochan.webnei.exporter.domain.asset.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import moe.takochan.webnei.exporter.domain.asset.AssetContract;
import moe.takochan.webnei.exporter.domain.asset.internal.AssetPath;
import moe.takochan.webnei.exporter.domain.asset.render.client.DynamicTextureState;
import moe.takochan.webnei.exporter.domain.asset.render.client.FboIconRenderer;

public final class FluidIconRenderer implements IAssetRenderer {

    private final FboIconRenderer fboRenderer = new FboIconRenderer();
    private final IconAnimator animator = new IconAnimator(fboRenderer);

    @Override
    public boolean supports(AssetRenderJob job) {
        return AssetContract.KIND_FLUID_ICON.equals(job.getKind()) && job.getFluidStack() != null;
    }

    @Override
    public IconTile prepareTile(final AssetRenderJob job) throws AssetRenderException {
        final FluidStack stack = job.getFluidStack();
        final IIcon icon = icon(stack);
        // 动画流体不可批量（需逐帧推进 atlas），退回 renderImage。
        if (DynamicTextureState.fromIcon(icon, TextureMap.locationBlocksTexture)
            .isStandardAtlasAnimation()) {
            return null;
        }
        return new IconTile(
            job,
            AssetPath.fluidIcon(job.getOwnerId()),
            FboIconRenderer.DEFAULT_WEB_ICON_SIZE,
            drawAction(stack, icon),
            AssetRenderMetadata.staticImage());
    }

    @Override
    public RenderedAsset renderImage(final AssetRenderJob job) throws AssetRenderException {
        FluidStack stack = job.getFluidStack();
        IIcon icon = icon(stack);
        DynamicTextureState dynamic = DynamicTextureState.fromIcon(icon, TextureMap.locationBlocksTexture);
        IconAnimator.RenderedIcon rendered = animator
            .render(dynamic, FboIconRenderer.DEFAULT_WEB_ICON_SIZE, drawAction(stack, icon));
        return RenderedAsset
            .png(job, AssetPath.fluidIcon(job.getOwnerId()), rendered.getImage(), rendered.getMetadataJson());
    }

    private static IIcon icon(FluidStack stack) throws AssetRenderException {
        IIcon icon = stack.getFluid()
            .getIcon(stack);
        if (icon == null) {
            throw new AssetRenderException(
                "Fluid has no icon: " + stack.getFluid()
                    .getName());
        }
        return icon;
    }

    private static FboIconRenderer.IconRenderAction drawAction(final FluidStack stack, final IIcon icon) {
        return new FboIconRenderer.IconRenderAction() {

            @Override
            public void render() {
                renderFluidIcon(stack, icon);
            }
        };
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
