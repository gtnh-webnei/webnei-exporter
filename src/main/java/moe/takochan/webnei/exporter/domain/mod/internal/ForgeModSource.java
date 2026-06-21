package moe.takochan.webnei.exporter.domain.mod.internal;

import java.util.List;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public final class ForgeModSource {

    private final ModRegistrar registrar;

    public ForgeModSource(ModRegistrar registrar) {
        this.registrar = registrar;
    }

    public void collect() {
        List<ModContainer> mods = Loader.instance()
            .getActiveModList();

        for (ModContainer mod : mods) {
            this.registrar.register(mod);
        }
    }
}
