package moe.takochan.webnei.exporter.bundle;

import moe.takochan.webnei.exporter.domain.ExportModelSet;

/** bundle 写出扩展点：把领域模型集合落成 TSV/JSON/PostgreSQL 等具体格式。 */
public interface IBundleWriter {

    BundleFormat format();

    BundleResult write(ExportModelSet models, BundleTarget target, BundleContext context) throws BundleException;
}
