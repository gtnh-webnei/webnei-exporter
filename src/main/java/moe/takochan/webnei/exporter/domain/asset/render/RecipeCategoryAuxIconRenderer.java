package moe.takochan.webnei.exporter.domain.asset.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL11;

import codechicken.nei.drawable.DrawableResource;
import moe.takochan.webnei.exporter.domain.asset.AssetContract;
import moe.takochan.webnei.exporter.domain.asset.internal.AssetPath;
import moe.takochan.webnei.exporter.domain.asset.render.client.FboIconRenderer;

/**
 * 渲染非 ItemStack 的配方分类图标：NEI handler 自绘贴图（{@link DrawableResource}）或文字兜底。
 *
 * <p>
 * ItemStack 来源由 {@link ItemIconRenderer} 处理；本渲染器只接 image / text 两种来源，二者由 {@link AssetRenderJob}
 * 的字段区分。图标均为静态，走批量 tile 路径。绘制在 GUI 正交投影（0..{@link FboIconRenderer#GUI_ICON_SIZE}）下进行，
 * 通过缩放把原始尺寸铺满画布。
 */
public final class RecipeCategoryAuxIconRenderer implements IAssetRenderer {

    @Override
    public boolean supports(AssetRenderJob job) {
        return AssetContract.KIND_RECIPE_CATEGORY_ICON.equals(job.getKind())
            && (job.getImage() != null || job.getFallbackText() != null);
    }

    @Override
    public IconTile prepareTile(AssetRenderJob job) {
        return new IconTile(
            job,
            AssetPath.recipeCategoryIcon(job.getOwnerId()),
            FboIconRenderer.DEFAULT_WEB_ICON_SIZE,
            drawAction(job),
            AssetRenderMetadata.staticImage());
    }

    @Override
    public RenderedAsset renderImage(AssetRenderJob job) throws AssetRenderException {
        IconTile tile = prepareTile(job);
        FboIconRenderer renderer = new FboIconRenderer();
        return RenderedAsset.png(
            job,
            tile.getRelativePath(),
            renderer.render(tile.getSize(), tile.getAction()),
            tile.getMetadataJson());
    }

    private static FboIconRenderer.IconRenderAction drawAction(final AssetRenderJob job) {
        if (job.getImage() != null) {
            return new FboIconRenderer.IconRenderAction() {

                @Override
                public void render() {
                    drawImage(job.getImage());
                }
            };
        }
        return new FboIconRenderer.IconRenderAction() {

            @Override
            public void render() {
                drawText(job.getFallbackText());
            }
        };
    }

    /** 把 DrawableResource 缩放铺满 GUI 画布后绘制；它自身负责绑定纹理与画 quad。 */
    private static void drawImage(DrawableResource image) {
        int width = Math.max(1, image.getWidth());
        int height = Math.max(1, image.getHeight());
        float scale = (float) FboIconRenderer.GUI_ICON_SIZE / Math.max(width, height);

        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        // 居中：缩放后图标尺寸 width*scale × height*scale，留白两侧均分。
        GL11.glTranslatef(
            (FboIconRenderer.GUI_ICON_SIZE - width * scale) / 2.0F,
            (FboIconRenderer.GUI_ICON_SIZE - height * scale) / 2.0F,
            0.0F);
        GL11.glScalef(scale, scale, 1.0F);
        image.draw(0, 0);
        GL11.glPopMatrix();
    }

    /** 在 GUI 画布中央绘制兜底文字，缩放到铺满，模仿 NEI 无图标 tab 的显示。 */
    private static void drawText(String text) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int textWidth = Math.max(1, font.getStringWidth(text));
        int textHeight = font.FONT_HEIGHT;
        float scale = (float) FboIconRenderer.GUI_ICON_SIZE / Math.max(textWidth, textHeight);

        GL11.glPushMatrix();
        GL11.glTranslatef(
            (FboIconRenderer.GUI_ICON_SIZE - textWidth * scale) / 2.0F,
            (FboIconRenderer.GUI_ICON_SIZE - textHeight * scale) / 2.0F,
            0.0F);
        GL11.glScalef(scale, scale, 1.0F);
        font.drawString(text, 0, 0, 0xFFFFFFFF);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}
