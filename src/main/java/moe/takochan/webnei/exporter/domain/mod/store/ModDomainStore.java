package moe.takochan.webnei.exporter.domain.mod.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.mod.ModExportModel;
import moe.takochan.webnei.exporter.domain.mod.model.ModRow;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/**
 * mod domain store — 纯数据持有和对外查询接口。
 */
public final class ModDomainStore implements IDomainStore {

    private final List<ModRow> rows = new ArrayList<>();

    public void add(ModRow row) {
        rows.add(row);
    }

    public ModRow findByModId(String modId) {
        for (ModRow row : rows) {
            if (row.getModId().equals(modId)) {
                return row;
            }
        }
        return null;
    }

    public List<ModRow> mods() {
        return Collections.unmodifiableList(rows);
    }

    @Override
    public IExportModel toExportModel() {
        return new ModExportModel(rows);
    }
}
