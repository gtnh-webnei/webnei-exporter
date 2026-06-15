package moe.takochan.webnei.exporter.bundle;

import moe.takochan.webnei.exporter.bundle.tsv.TsvBundleWriter;

/** 根据导出请求中指定的 bundle format 选择具体 writer。 */
public final class BundleWriterRegistry {

    public static BundleWriterRegistry defaults() {
        return new BundleWriterRegistry();
    }

    public IBundleWriter writerFor(BundleFormat format) throws BundleException {
        if (format == BundleFormat.TSV) {
            return new TsvBundleWriter();
        }
        throw new BundleException("Unsupported bundle format: " + format);
    }
}
