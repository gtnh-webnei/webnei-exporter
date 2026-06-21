package moe.takochan.webnei.exporter.domain.asset.render;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

final class PngAssetFile {

    private PngAssetFile() {}

    /** 将图标编码为 PNG 字节，不落盘；落盘由 zip writer 统一完成。 */
    static byte[] encode(BufferedImage image) throws AssetRenderException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", out);
        } catch (IOException e) {
            throw new AssetRenderException("Unable to encode PNG", e);
        }
        return out.toByteArray();
    }
}
