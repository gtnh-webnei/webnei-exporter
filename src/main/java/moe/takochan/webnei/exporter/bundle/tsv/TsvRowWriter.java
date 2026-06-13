package moe.takochan.webnei.exporter.bundle.tsv;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public final class TsvRowWriter implements Closeable {

    private final BufferedWriter writer;

    public TsvRowWriter(File file) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(file));
    }

    public void writeRow(List<String> values) throws IOException {
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                writer.write('\t');
            }
            writer.write(cleanCell(values.get(i)));
        }
        writer.newLine();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    private static String cleanCell(String value) {
        return value == null ? ""
            : value.replace('\t', ' ')
                .replace('\r', ' ')
                .replace('\n', ' ');
    }
}
