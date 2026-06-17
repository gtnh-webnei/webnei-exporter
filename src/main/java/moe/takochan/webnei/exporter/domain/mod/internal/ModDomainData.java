package moe.takochan.webnei.exporter.domain.mod.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.mod.ModExportModel;
import moe.takochan.webnei.exporter.domain.mod.model.ModRow;

/**
 * mod domain store 的内部数据。
 *
 * <p>
 * 仅 mod domain 自己直接使用该类；其他 domain 需要 mod 信息时应通过 ModDomainStore 的公开接口访问。
 */
public final class ModDomainData {

    private final List<ModRow> rows = new ArrayList<>();

    public void add(ModRow row) {
        rows.add(row);
    }

    public ModRow findByModId(String modId) {
        for (ModRow row : rows) {
            if (row.getModId()
                .equals(modId)) {
                return row;
            }
        }
        return null;
    }

    public List<ModRow> mods() {
        return Collections.unmodifiableList(rows);
    }

    public IExportModel toExportModel() {
        return new ModExportModel(rows);
    }
}
