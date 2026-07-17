package moe.takochan.webnei.exporter.bundle.tsv;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class TsvRowWriter implements Closeable {

    private final BufferedWriter writer;

    public TsvRowWriter(File file) throws IOException {
        this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
    }

    public void writeRow(List<String> values) throws IOException {
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                writer.write('\t');
            }
            writer.write(TsvCellCodec.encode(values.get(i)));
        }
        writer.newLine();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
