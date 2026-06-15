package moe.takochan.webnei.exporter.domain.mod.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import moe.takochan.webnei.exporter.domain.dataset.store.DatasetDomainStore;
import moe.takochan.webnei.exporter.domain.mod.store.ModDomainStore;
import moe.takochan.webnei.exporter.engine.task.ExportTaskContext;
import moe.takochan.webnei.exporter.engine.task.IExportTask;

/**
 * mod 数据域导出任务。
 *
 * <p>
 * 扫描当前 Forge 已加载的 mod 列表，为每个 mod 创建 ModRow 并注册 ModDomainStore。
 * 依赖 DatasetDomainStore（需要 dataset_id）。
 */
public final class ModExportTask implements IExportTask {

    public static final String ID = "mod-export";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String labelKey() {
        return "webnei.task.mods";
    }

    @Override
    public void execute(ExportTaskContext context) {
        String datasetId = context.store(DatasetDomainStore.class).get(null).getDatasetId();

        ModDomainStore store = new ModDomainStore(datasetId);
        List<ModContainer> mods = new ArrayList<>(Loader.instance().getActiveModList());
        mods.sort(Comparator.comparing((ModContainer m) -> nullToEmpty(m.getModId()))
            .thenComparing(m -> m.getSource() == null ? "" : m.getSource().getName()));

        for (ModContainer mod : mods) {
            store.add(mod);
        }

        context.register(ModDomainStore.class, store);
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
