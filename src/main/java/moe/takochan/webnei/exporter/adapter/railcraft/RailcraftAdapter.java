package moe.takochan.webnei.exporter.adapter.railcraft;

import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.adapter.AdapterContext;
import moe.takochan.webnei.exporter.adapter.AdapterLoadingSource;
import moe.takochan.webnei.exporter.adapter.AdapterResult;
import moe.takochan.webnei.exporter.adapter.IModAdapter;
import moe.takochan.webnei.exporter.nei.scan.NeiHandlerEntry;
import tonius.neiintegration.RecipeHandlerBase;
import tonius.neiintegration.mods.railcraft.RecipeHandlerBlastFurnace;
import tonius.neiintegration.mods.railcraft.RecipeHandlerCokeOven;
import tonius.neiintegration.mods.railcraft.RecipeHandlerRollingMachine;

public final class RailcraftAdapter implements IModAdapter {

    private static final String ID = "railcraft";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public boolean supportsNeiHandler(NeiHandlerEntry entry) {
        return entry.handler instanceof RecipeHandlerBlastFurnace || entry.handler instanceof RecipeHandlerCokeOven
            || entry.handler instanceof RecipeHandlerRollingMachine;
    }

    @Override
    public AdapterResult extractNeiHandler(NeiHandlerEntry entry, AdapterContext context) {
        if (!(entry.handler instanceof RecipeHandlerBase handler)) {
            return AdapterResult.unsupported();
        }

        String key = handler.getRecipeID();
        try {
            IRecipeHandler loaded = handler.getRecipeHandler(key);
            if (loaded == null) {
                return AdapterResult.error(ID, AdapterLoadingSource.HANDLER_RECIPE_ID, key, "loaded handler is null");
            }
            return AdapterResult.extracted(ID, loaded, AdapterLoadingSource.HANDLER_RECIPE_ID, key);
        } catch (Throwable t) {
            return AdapterResult.error(ID, AdapterLoadingSource.HANDLER_RECIPE_ID, key, errorReason(t));
        }
    }

    private static String errorReason(Throwable t) {
        return t.getClass()
            .getSimpleName() + (t.getMessage() == null ? "" : ": " + t.getMessage());
    }
}
