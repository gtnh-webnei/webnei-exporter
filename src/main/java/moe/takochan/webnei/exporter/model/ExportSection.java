package moe.takochan.webnei.exporter.model;

import java.util.Collections;
import java.util.List;

/** Named table-like section inside an export dataset. */
public final class ExportSection {

    public final String name;
    public final List<String> columns;
    public final List<ExportRow> rows;

    public ExportSection(String name, List<String> columns, List<ExportRow> rows) {
        this.name = name;
        this.columns = Collections.unmodifiableList(columns);
        this.rows = Collections.unmodifiableList(rows);
    }
}
