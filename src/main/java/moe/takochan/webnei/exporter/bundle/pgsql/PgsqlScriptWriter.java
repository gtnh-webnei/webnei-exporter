package moe.takochan.webnei.exporter.bundle.pgsql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import moe.takochan.webnei.exporter.bundle.BundleException;
import moe.takochan.webnei.exporter.bundle.record.BundleRecordSet;

/** 写出 psql 可执行的数据导入脚本。 */
final class PgsqlScriptWriter {

    private static final List<String> RECORD_SET_ORDER = Arrays.asList(
        "dataset",
        "mod",
        "item",
        "item_variant",
        "item_tool_class",
        "item_list_entry",
        "fluid",
        "fluid_container",
        "fluid_block",
        "ore_dictionary",
        "ore_dictionary_entry",
        "asset");
    private static final String DATASET_RECORD_SET = "dataset";
    private static final String DATASET_ID_FIELD = "dataset_id";
    private static final String COPY_NULL = "\\N";
    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

    void write(List<BundleRecordSet> recordSets, File file) throws BundleException, IOException {
        Map<String, BundleRecordSet> byName = recordSetsByName(recordSets);
        String datasetId = datasetId(byName.get(DATASET_RECORD_SET));

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write("\\set ON_ERROR_STOP on\n");
            writer.write("-- Requires webnei-exporter/schema.sql to be applied before running this script.\n");
            writer.write("BEGIN;\n\n");
            writer.write("DELETE FROM dataset WHERE dataset_id = ");
            writer.write(sqlStringLiteral(datasetId));
            writer.write(";\n\n");

            for (String name : RECORD_SET_ORDER) {
                BundleRecordSet recordSet = byName.remove(name);
                if (recordSet != null) {
                    writeCopy(writer, recordSet);
                }
            }
            if (!byName.isEmpty()) {
                throw new BundleException(
                    "No PostgreSQL COPY order for record set: " + byName.keySet()
                        .iterator()
                        .next());
            }

            writer.write("COMMIT;\n");
        }
    }

    private static Map<String, BundleRecordSet> recordSetsByName(List<BundleRecordSet> recordSets)
        throws BundleException {
        Map<String, BundleRecordSet> byName = new LinkedHashMap<>();
        for (BundleRecordSet recordSet : recordSets) {
            String name = recordSet.getName();
            validateIdentifier(name);
            if (byName.put(name, recordSet) != null) {
                throw new BundleException("Duplicate bundle record set: " + name);
            }
        }
        return byName;
    }

    private static String datasetId(BundleRecordSet dataset) throws BundleException {
        if (dataset == null || dataset.getRecords()
            .isEmpty()) {
            throw new BundleException("PostgreSQL script requires dataset record set");
        }
        int datasetIdIndex = dataset.getFields()
            .indexOf(DATASET_ID_FIELD);
        if (datasetIdIndex < 0) {
            throw new BundleException("PostgreSQL script requires dataset_id field");
        }
        return dataset.getRecords()
            .get(0)
            .get(datasetIdIndex);
    }

    private static void writeCopy(Writer writer, BundleRecordSet recordSet) throws IOException, BundleException {
        writer.write("COPY ");
        writer.write(recordSet.getName());
        writer.write(" (");
        for (int i = 0; i < recordSet.getFields()
            .size(); i++) {
            if (i > 0) {
                writer.write(", ");
            }
            String field = recordSet.getFields()
                .get(i);
            validateIdentifier(field);
            writer.write(field);
        }
        writer.write(") FROM stdin;\n");
        for (List<String> record : recordSet.getRecords()) {
            writeCopyRecord(writer, record);
        }
        writer.write("\\.\n\n");
    }

    private static void writeCopyRecord(Writer writer, List<String> record) throws IOException {
        for (int i = 0; i < record.size(); i++) {
            if (i > 0) {
                writer.write('\t');
            }
            writer.write(copyValue(record.get(i)));
        }
        writer.write('\n');
    }

    private static String copyValue(String value) {
        if (value == null) {
            return COPY_NULL;
        }
        StringBuilder escaped = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\\') {
                escaped.append("\\\\");
            } else if (c == '\t') {
                escaped.append("\\t");
            } else if (c == '\n') {
                escaped.append("\\n");
            } else if (c == '\r') {
                escaped.append("\\r");
            } else {
                escaped.append(c);
            }
        }
        return escaped.toString();
    }

    private static String sqlStringLiteral(String value) {
        return "'" + value.replace("'", "''") + "'";
    }

    private static void validateIdentifier(String identifier) throws BundleException {
        if (!IDENTIFIER.matcher(identifier)
            .matches()) {
            throw new BundleException("Unsafe PostgreSQL identifier: " + identifier);
        }
    }
}
