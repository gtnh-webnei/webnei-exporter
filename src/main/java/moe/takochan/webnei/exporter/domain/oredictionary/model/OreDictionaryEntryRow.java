package moe.takochan.webnei.exporter.domain.oredictionary.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** ore_dictionary_entry 表行：字典名与 item variant 的展开后关联。 */
@Getter
@RequiredArgsConstructor
public final class OreDictionaryEntryRow {

    /** 所属 dataset ID。 */
    private final String datasetId;

    /** Forge OreDictionary 字典名。 */
    private final String dictionaryName;

    /** 关联的 item variant ID。 */
    private final String itemVariantId;

    /** 当前字典名下的展示顺序。 */
    private final int listIndex;
}
