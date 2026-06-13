package moe.takochan.webnei.exporter.nei.recipe;

import moe.takochan.webnei.exporter.nei.scan.NeiHandlerDescriptor;

public final class ExtractedHandler {

    public final NeiHandlerDescriptor descriptor;
    public final int recipeCount;
    public final String status;
    public final String reason;

    ExtractedHandler(NeiHandlerDescriptor descriptor, int recipeCount, String status, String reason) {
        this.descriptor = descriptor;
        this.recipeCount = recipeCount;
        this.status = status;
        this.reason = reason;
    }
}
