package moe.takochan.webnei.exporter.domain.mod;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.mod.model.ModRow;

/** mod 数据域领域模型。 */
@Getter
public final class ModExportModel implements IExportModel {

    public static final String TYPE = "mod";

    private final List<ModRow> mods;

    public ModExportModel(List<ModRow> mods) {
        this.mods = Collections.unmodifiableList(mods);
    }

    @Override
    public String type() {
        return TYPE;
    }
}
