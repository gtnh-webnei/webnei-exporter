package moe.takochan.webnei.exporter.domain.nei.loading;

import moe.takochan.webnei.exporter.domain.nei.scan.NeiHandlerEntry;

public interface INeiLoadingSupport {

    boolean supports(NeiHandlerEntry entry);

    NeiLoadingResult load(NeiHandlerEntry entry);
}
