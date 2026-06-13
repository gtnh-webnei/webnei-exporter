package moe.takochan.webnei.exporter.bundle.tsv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import moe.takochan.webnei.exporter.bundle.BundleContext;
import moe.takochan.webnei.exporter.bundle.BundleException;
import moe.takochan.webnei.exporter.bundle.BundleFormat;
import moe.takochan.webnei.exporter.bundle.BundleResult;
import moe.takochan.webnei.exporter.bundle.BundleTarget;
import moe.takochan.webnei.exporter.bundle.IBundleWriter;
import moe.takochan.webnei.exporter.model.ExportDataset;
import moe.takochan.webnei.exporter.model.ExportRow;
import moe.takochan.webnei.exporter.model.ExportSection;

/** Writes each dataset section as one TSV file under the dataset output directory. */
public final class TsvBundleWriter implements IBundleWriter {

    @Override
    public BundleFormat format() {
        return BundleFormat.TSV;
    }

    @Override
    public BundleResult write(ExportDataset dataset, BundleTarget target, BundleContext context)
        throws BundleException {
        File datasetDirectory = new File(target.outputDirectory, dataset.name);
        if (!datasetDirectory.isDirectory() && !datasetDirectory.mkdirs()) {
            throw new BundleException("Unable to create bundle directory: " + datasetDirectory.getAbsolutePath());
        }

        List<String> files = new ArrayList<>();
        for (ExportSection section : dataset.sections) {
            File file = new File(datasetDirectory, section.name + ".tsv");
            writeSection(section, file);
            files.add(file.getAbsolutePath());
        }
        return BundleResult.success(format(), files);
    }

    private static void writeSection(ExportSection section, File file) throws BundleException {
        try (TsvRowWriter writer = new TsvRowWriter(file)) {
            writer.writeRow(section.columns);
            for (ExportRow row : section.rows) {
                writer.writeRow(row.values);
            }
        } catch (IOException e) {
            throw new BundleException("Unable to write TSV section: " + file.getAbsolutePath(), e);
        }
    }
}
