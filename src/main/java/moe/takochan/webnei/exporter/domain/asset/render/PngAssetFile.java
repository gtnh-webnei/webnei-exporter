package moe.takochan.webnei.exporter.domain.asset.render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

final class PngAssetFile {

    private PngAssetFile() {}

    static String write(BufferedImage image, File file) throws AssetRenderException {
        ensureParent(file);
        try {
            ImageIO.write(image, "png", file);
            return ImageFileHasher.sha256(file);
        } catch (IOException e) {
            throw new AssetRenderException("Unable to write PNG: " + file.getAbsolutePath(), e);
        }
    }

    private static void ensureParent(File file) throws AssetRenderException {
        File parent = file.getParentFile();
        if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
            throw new AssetRenderException("Unable to create asset directory: " + parent.getAbsolutePath());
        }
    }
}
