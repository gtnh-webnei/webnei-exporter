package moe.takochan.webnei.exporter.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ExportRow {

    public final List<String> values;

    private ExportRow(List<String> values) {
        this.values = Collections.unmodifiableList(values);
    }

    public static ExportRow of(String... values) {
        return new ExportRow(Arrays.asList(values));
    }
}
