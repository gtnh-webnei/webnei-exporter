package moe.takochan.webnei.exporter.domain.asset.render;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 把图标 PNG 字节流式追加进单个 zip。
 *
 * <p>
 * 用 {@link ZipEntry#STORED} 不二次压缩（PNG 已是压缩数据），entry 名即 bundle 内相对路径。CRC32 与
 * 大小由调用方（encoder 线程）在并行阶段算好；本类仅对 {@link ZipOutputStream} 的追加做同步——该流
 * 非线程安全，但追加本身只是 memcpy，临界区极短，不影响并行编码的吞吐。
 */
final class ZipAssetWriter implements Closeable {

    private final ZipOutputStream zip;

    private ZipAssetWriter(ZipOutputStream zip) {
        this.zip = zip;
    }

    static ZipAssetWriter create(File file) throws IOException {
        ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        zip.setMethod(ZipOutputStream.STORED);
        return new ZipAssetWriter(zip);
    }

    /** 并行阶段：编码 PNG 字节并构造好 STORED entry（含 CRC32 与大小）。 */
    static ZipEntry storedEntry(String name, byte[] data) {
        CRC32 crc = new CRC32();
        crc.update(data);
        ZipEntry entry = new ZipEntry(name);
        entry.setMethod(ZipEntry.STORED);
        entry.setSize(data.length);
        entry.setCompressedSize(data.length);
        entry.setCrc(crc.getValue());
        return entry;
    }

    /** 串行阶段：把一个已就绪的 entry 追加进 zip。 */
    synchronized void writeEntry(ZipEntry entry, byte[] data) throws IOException {
        zip.putNextEntry(entry);
        zip.write(data);
        zip.closeEntry();
    }

    @Override
    public synchronized void close() throws IOException {
        zip.close();
    }
}
