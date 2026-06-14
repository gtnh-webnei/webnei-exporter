package moe.takochan.webnei.exporter.adapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import moe.takochan.webnei.exporter.adapter.railcraft.RailcraftAdapter;
import moe.takochan.webnei.exporter.nei.scan.NeiHandlerEntry;

public final class AdapterRegistry {

    private final List<IModAdapter> adapters;

    public AdapterRegistry(List<IModAdapter> adapters) {
        this.adapters = Collections.unmodifiableList(adapters);
    }

    public static AdapterRegistry defaults() {
        return new AdapterRegistry(Arrays.<IModAdapter>asList(new RailcraftAdapter()));
    }

    public AdapterResult extractNeiHandler(NeiHandlerEntry entry, AdapterContext context) {
        for (IModAdapter adapter : adapters) {
            if (adapter.supportsNeiHandler(entry)) {
                return adapter.extractNeiHandler(entry, context);
            }
        }
        return AdapterResult.unsupported();
    }
}
