package moe.takochan.webnei.exporter.domain.oredictionary;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.oredictionary.model.OreDictionaryEntryRow;
import moe.takochan.webnei.exporter.domain.oredictionary.model.OreDictionaryRow;

/** ore_dictionary 数据域中间模型。 */
@Getter
public final class OreDictionaryExportModel implements IExportModel {

    /** 模型类型标识，供 bundle writer 选择具体映射逻辑。 */
    public static final String TYPE = "ore_dictionary";

    /** OreDictionary dictionary name 行。 */
    private final List<OreDictionaryRow> dictionaries;

    /** OreDictionary dictionary name 到 item variant 的展开后关联行。 */
    private final List<OreDictionaryEntryRow> entries;

    public OreDictionaryExportModel(List<OreDictionaryRow> dictionaries, List<OreDictionaryEntryRow> entries) {
        this.dictionaries = Collections.unmodifiableList(dictionaries);
        this.entries = Collections.unmodifiableList(entries);
    }

    @Override
    public String type() {
        return TYPE;
    }
}
