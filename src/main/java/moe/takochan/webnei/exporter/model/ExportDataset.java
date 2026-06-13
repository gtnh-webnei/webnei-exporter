package moe.takochan.webnei.exporter.model;

import java.util.Collections;
import java.util.List;

/** Format-independent export payload consumed by bundle writers. */
public final class ExportDataset {

    public final String name;
    public final List<ExportSection> sections;

    public ExportDataset(String name, List<ExportSection> sections) {
        this.name = name;
        this.sections = Collections.unmodifiableList(sections);
    }
}
