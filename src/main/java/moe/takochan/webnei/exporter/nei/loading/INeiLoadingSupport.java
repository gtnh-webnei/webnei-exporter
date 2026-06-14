package moe.takochan.webnei.exporter.nei.loading;

import moe.takochan.webnei.exporter.nei.scan.NeiHandlerEntry;

public interface INeiLoadingSupport {

    boolean supports(NeiHandlerEntry entry);

    NeiLoadingResult load(NeiHandlerEntry entry);
}
