package moe.takochan.webnei.exporter.domain.asset.render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

final class PngAssetFile {

    private PngAssetFile() {}

    static void write(BufferedImage image, File file) throws AssetRenderException {
        ensureParent(file);
        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            throw new AssetRenderException("Unable to write PNG: " + file.getAbsolutePath(), e);
        }
    }

    private static void ensureParent(File file) throws AssetRenderException {
        File parent = file.getParentFile();
        if (parent == null || parent.isDirectory()) {
            return;
        }
        // 多个 encoder 线程并发写盘，mkdirs 可能因竞争返回 false；再确认一次目录是否已存在。
        if (!parent.mkdirs() && !parent.isDirectory()) {
            throw new AssetRenderException("Unable to create asset directory: " + parent.getAbsolutePath());
        }
    }
}
