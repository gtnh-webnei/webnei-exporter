package moe.takochan.webnei.exporter.bundle;

import moe.takochan.webnei.exporter.model.ExportDataset;

/** Writes a format-independent export dataset to a concrete bundle format. */
public interface IBundleWriter {

    BundleFormat format();

    BundleResult write(ExportDataset dataset, BundleTarget target, BundleContext context) throws BundleException;
}
