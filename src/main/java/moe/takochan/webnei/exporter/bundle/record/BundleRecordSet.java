package moe.takochan.webnei.exporter.bundle.record;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 一个 bundle record set 的字段定义和记录数据。 */
@Getter
@RequiredArgsConstructor
public final class BundleRecordSet {

    /** record set 名称。 */
    private final String name;

    /** 写出顺序键，值即 FK 依赖层级（父表小、子表大）；writer 按此升序写出。 */
    private final int order;

    /** 有序字段名。 */
    private final List<String> fields;

    /** 记录行。 */
    private final List<List<String>> records;
}
