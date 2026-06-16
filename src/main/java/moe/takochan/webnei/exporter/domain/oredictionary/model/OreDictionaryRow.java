package moe.takochan.webnei.exporter.domain.oredictionary.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** ore_dictionary 表行：Forge OreDictionary 中注册过的字典名。 */
@Getter
@RequiredArgsConstructor
public final class OreDictionaryRow {

    /** 所属 dataset ID。 */
    private final String datasetId;

    /** Forge OreDictionary 字典名。 */
    private final String dictionaryName;
}
