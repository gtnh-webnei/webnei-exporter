package moe.takochan.webnei.exporter.bundle.pgsql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import moe.takochan.webnei.exporter.bundle.BundleException;
import moe.takochan.webnei.exporter.bundle.record.BundleRecordSet;

/** 写出 psql 可执行的数据导入脚本。 */
final class PgsqlScriptWriter {

    private static final String DATASET_RECORD_SET = "dataset";
    private static final String DATASET_ID_FIELD = "dataset_id";
    private static final String COPY_NULL = "\\N";
    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

    void write(List<BundleRecordSet> recordSets, File file) throws BundleException, IOException {
        List<BundleRecordSet> ordered = orderedByDependency(recordSets);
        String datasetId = datasetId(findByName(ordered, DATASET_RECORD_SET));

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write("\\set ON_ERROR_STOP on\n");
            writer.write("-- Requires webnei-exporter/schema.sql to be applied before running this script.\n");
            writer.write("BEGIN;\n\n");
            writer.write("DELETE FROM dataset WHERE dataset_id = ");
            writer.write(sqlStringLiteral(datasetId));
            writer.write(";\n\n");

            for (BundleRecordSet recordSet : ordered) {
                writeCopy(writer, recordSet);
            }

            writer.write("COMMIT;\n");

            // 搜索物化视图不随 COPY 更新，数据提交后须刷新。CONCURRENTLY 要求视图有唯一索引，
            // 且不能在事务块内执行，故置于 COMMIT 之后。
            writer.write("\nREFRESH MATERIALIZED VIEW CONCURRENTLY mv_item_search;\n");
            writer.write("REFRESH MATERIALIZED VIEW CONCURRENTLY mv_fluid_search;\n");
        }
    }

    /**
     * 按 record set 自带的 {@code order}（FK 依赖层级）升序排序，同序按名字保证稳定。
     *
     * <p>
     * 顺序信息随各 record set 定义而来，writer 不再维护中央表名顺序列表，新增表只需在其 spec 上声明 order。
     */
    private static List<BundleRecordSet> orderedByDependency(List<BundleRecordSet> recordSets) throws BundleException {
        List<BundleRecordSet> ordered = new ArrayList<>(recordSets);
        for (BundleRecordSet recordSet : ordered) {
            validateIdentifier(recordSet.getName());
        }
        ordered.sort(
            Comparator.comparingInt(BundleRecordSet::getOrder)
                .thenComparing(BundleRecordSet::getName));
        for (int i = 1; i < ordered.size(); i++) {
            if (ordered.get(i)
                .getName()
                .equals(
                    ordered.get(i - 1)
                        .getName())) {
                throw new BundleException(
                    "Duplicate bundle record set: " + ordered.get(i)
                        .getName());
            }
        }
        return ordered;
    }

    private static BundleRecordSet findByName(List<BundleRecordSet> recordSets, String name) {
        for (BundleRecordSet recordSet : recordSets) {
            if (recordSet.getName()
                .equals(name)) {
                return recordSet;
            }
        }
        return null;
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
