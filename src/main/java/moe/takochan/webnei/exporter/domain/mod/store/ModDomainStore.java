package moe.takochan.webnei.exporter.domain.mod.store;

import java.util.List;

import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.mod.internal.ModDomainData;
import moe.takochan.webnei.exporter.domain.mod.model.ModRow;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

/**
 * mod domain store — mod domain 的跨 domain 交互边界。
 *
 * <p>
 * 该类只暴露其他 domain 允许依赖的公开 API，内部列表和导出模型组装细节由 ModDomainData 维护。
 */
public final class ModDomainStore implements IDomainStore {

    private final ModDomainData data = new ModDomainData();

    public void add(ModRow row) {
        data.add(row);
    }

    public ModRow findByModId(String modId) {
        return data.findByModId(modId);
    }

    public List<ModRow> mods() {
        return data.mods();
    }

    @Override
    public IExportModel toExportModel() {
        return data.toExportModel();
    }
}
