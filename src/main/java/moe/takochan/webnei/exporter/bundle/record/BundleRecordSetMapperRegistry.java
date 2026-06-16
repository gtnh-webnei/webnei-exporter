package moe.takochan.webnei.exporter.bundle.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import moe.takochan.webnei.exporter.bundle.record.mapper.DatasetRecordSetMapper;
import moe.takochan.webnei.exporter.bundle.record.mapper.ItemRecordSetMapper;
import moe.takochan.webnei.exporter.bundle.record.mapper.ModRecordSetMapper;
import moe.takochan.webnei.exporter.bundle.record.mapper.OreDictionaryRecordSetMapper;
import moe.takochan.webnei.exporter.domain.IExportModel;

/** bundle record set mapper 注册表。 */
public final class BundleRecordSetMapperRegistry {

    private final Map<Class<? extends IExportModel>, IBundleRecordSetMapper<?>> mappers;

    public BundleRecordSetMapperRegistry(List<IBundleRecordSetMapper<?>> mappers) {
        Map<Class<? extends IExportModel>, IBundleRecordSetMapper<?>> index = new LinkedHashMap<>();
        for (IBundleRecordSetMapper<?> mapper : mappers) {
            index.put(mapper.modelType(), mapper);
        }
        this.mappers = Collections.unmodifiableMap(index);
    }

    /** 创建内置 mapper 注册表。 */
    public static BundleRecordSetMapperRegistry defaults() {
        List<IBundleRecordSetMapper<?>> mappers = new ArrayList<>();
        mappers.add(new DatasetRecordSetMapper());
        mappers.add(new ModRecordSetMapper());
        mappers.add(new ItemRecordSetMapper());
        mappers.add(new OreDictionaryRecordSetMapper());
        return new BundleRecordSetMapperRegistry(mappers);
    }

    /** 替换指定 model 类型的 mapper。 */
    public BundleRecordSetMapperRegistry replace(IBundleRecordSetMapper<?> mapper) {
        Map<Class<? extends IExportModel>, IBundleRecordSetMapper<?>> index = new LinkedHashMap<>(mappers);
        index.put(mapper.modelType(), mapper);
        return new BundleRecordSetMapperRegistry(new ArrayList<>(index.values()));
    }

    /** 查找支持该 model 的 mapper。 */
    public IBundleRecordSetMapper<?> mapperFor(IExportModel model) {
        IBundleRecordSetMapper<?> mapper = mappers.get(model.getClass());
        if (mapper != null) {
            return mapper;
        }
        for (IBundleRecordSetMapper<?> candidate : mappers.values()) {
            if (candidate.modelType()
                .isInstance(model)) {
                return candidate;
            }
        }
        return null;
    }
}
