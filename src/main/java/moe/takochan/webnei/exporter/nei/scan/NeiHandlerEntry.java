package moe.takochan.webnei.exporter.nei.scan;

import codechicken.nei.recipe.IRecipeHandler;

public final class NeiHandlerEntry {

    public final NeiHandlerDescriptor descriptor;
    public final IRecipeHandler handler;

    public NeiHandlerEntry(NeiHandlerDescriptor descriptor, IRecipeHandler handler) {
        this.descriptor = descriptor;
        this.handler = handler;
    }
}
