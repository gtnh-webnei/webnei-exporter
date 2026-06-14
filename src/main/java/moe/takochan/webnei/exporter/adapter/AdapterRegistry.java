package moe.takochan.webnei.exporter.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import moe.takochan.webnei.exporter.adapter.railcraft.RailcraftAdapter;
import moe.takochan.webnei.exporter.nei.scan.NeiHandlerEntry;

public final class AdapterRegistry {

    private final List<IModAdapter> adapters;

    public AdapterRegistry(List<IModAdapter> adapters) {
        this.adapters = Collections.unmodifiableList(available(adapters));
    }

    public static AdapterRegistry defaults() {
        List<IModAdapter> adapters = new ArrayList<>();
        adapters.add(new RailcraftAdapter());
        return new AdapterRegistry(adapters);
    }

    private static List<IModAdapter> available(List<IModAdapter> adapters) {
        List<IModAdapter> out = new ArrayList<>();
        for (IModAdapter adapter : adapters) {
            if (adapter.isAvailable()) {
                out.add(adapter);
            }
        }
        return out;
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
