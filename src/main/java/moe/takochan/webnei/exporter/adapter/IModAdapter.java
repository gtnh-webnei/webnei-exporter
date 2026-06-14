package moe.takochan.webnei.exporter.adapter;

import moe.takochan.webnei.exporter.nei.scan.NeiHandlerEntry;

public interface IModAdapter {

    String id();

    boolean supportsNeiHandler(NeiHandlerEntry entry);

    AdapterResult extractNeiHandler(NeiHandlerEntry entry, AdapterContext context);
}
