package moe.takochan.webnei.exporter.domain.dataset.internal;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.dataset.DatasetExportModel;
import moe.takochan.webnei.exporter.domain.dataset.model.DatasetRow;
import moe.takochan.webnei.exporter.engine.store.IDomainData;

/**
 * dataset domain store 的内部结果集。
 *
 * <p>
 * 该类只持有 dataset row 与导出读取能力；注册编排职责由 DatasetRegistrar 负责。
 */
public final class DatasetDomainData implements IDomainData {

    private DatasetRow row;

    void setRow(DatasetRow row) {
        this.row = row;
    }

    public String datasetId() {
        if (row == null) {
            return null;
        }
        return row.getDatasetId();
    }

    public String language() {
        if (row == null) {
            return null;
        }
        return row.getLanguage();
    }

    @Override
    public IExportModel toExportModel() {
        return row == null ? null : new DatasetExportModel(row);
    }
}
