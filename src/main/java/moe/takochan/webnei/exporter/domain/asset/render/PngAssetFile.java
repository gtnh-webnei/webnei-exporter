package moe.takochan.webnei.exporter.domain.asset.render;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

final class PngAssetFile {

    private static final String PNG_FORMAT = "png";
    private static final String PNG_COMPRESSION_TYPE = "Deflate";
    private static final float HIGH_COMPRESSION_QUALITY = 0.0f;

    private PngAssetFile() {}

    /** 将图标编码为 PNG 字节，不落盘；落盘由 zip writer 统一完成。 */
    static byte[] encode(BufferedImage image) throws AssetRenderException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(PNG_FORMAT);
        if (!writers.hasNext()) {
            throw new AssetRenderException("Unable to find PNG writer");
        }

        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionType(PNG_COMPRESSION_TYPE);
        param.setCompressionQuality(HIGH_COMPRESSION_QUALITY);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageOutputStream imageOut = ImageIO.createImageOutputStream(out)) {
            writer.setOutput(imageOut);
            writer.write(null, new IIOImage(image, null, null), param);
            imageOut.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new AssetRenderException("Unable to encode PNG", e);
        } finally {
            writer.dispose();
        }
    }
}
