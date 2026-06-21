package moe.takochan.webnei.exporter.domain.mod.internal;

import java.util.*;

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

    private final Map<String, ModRow> mods = new LinkedHashMap<>();

    public void register(ModRow row) {
        mods.putIfAbsent(row.getModId(), row);
    }

    public ModRow findByModId(String modId) {
        return mods.get(modId);
    }

    public IExportModel toExportModel() {
        return new ModExportModel(new ArrayList<>(mods.values()));
    }
}
