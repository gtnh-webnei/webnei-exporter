package moe.takochan.webnei.exporter.bundle.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/** bundle record set 的字段定义和记录映射规则。 */
public final class BundleRecordSetSpec<T> {

    private final String name;
    private final List<Field<T>> fields = new ArrayList<>();

    private BundleRecordSetSpec(String name) {
        this.name = name;
    }

    /** 创建 record set 映射规则。 */
    public static <T> BundleRecordSetSpec<T> recordSet(String name) {
        return new BundleRecordSetSpec<>(name);
    }

    /** 添加一个字段。 */
    public BundleRecordSetSpec<T> field(String name, Function<T, ?> valueMapper) {
        fields.add(new Field<>(name, valueMapper));
        return this;
    }

    /** 映射单条记录。 */
    public BundleRecordSet records(T sourceRecord) {
        return records(Collections.singletonList(sourceRecord));
    }

    /** 映射多条记录。 */
    public BundleRecordSet records(List<T> sourceRecords) {
        List<String> fieldNames = new ArrayList<>();
        for (Field<T> field : fields) {
            fieldNames.add(field.name);
        }

        List<List<String>> records = new ArrayList<>();
        for (T sourceRecord : sourceRecords) {
            List<String> values = new ArrayList<>();
            for (Field<T> field : fields) {
                values.add(field.value(sourceRecord));
            }
            records.add(values);
        }
        return new BundleRecordSet(name, fieldNames, records);
    }

    /** 单字段映射规则。 */
    private static final class Field<T> {

        private final String name;
        private final Function<T, ?> valueMapper;

        private Field(String name, Function<T, ?> valueMapper) {
            this.name = name;
            this.valueMapper = valueMapper;
        }

        private String value(T sourceRecord) {
            Object value = valueMapper.apply(sourceRecord);
            return value == null ? null : value.toString();
        }
    }
}
