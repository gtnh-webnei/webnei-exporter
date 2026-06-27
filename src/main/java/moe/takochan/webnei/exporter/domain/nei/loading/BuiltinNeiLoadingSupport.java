package moe.takochan.webnei.exporter.domain.nei.loading;

import codechicken.nei.recipe.BrewingRecipeHandler;
import codechicken.nei.recipe.FireworkRecipeHandler;
import codechicken.nei.recipe.FuelRecipeHandler;
import codechicken.nei.recipe.FurnaceRecipeHandler;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.ShapedRecipeHandler;
import codechicken.nei.recipe.ShapelessRecipeHandler;
import moe.takochan.webnei.exporter.domain.nei.scan.NeiHandlerEntry;

/**
 * 实验性 NEI 探测/抽取代码；当前未接入 ExportPlan.ALL 的正式导出流程。
 * 请勿在正式导出链路中引用，仅供参考。
 * 内建 NEI handler 加载支持实现。
 */
public final class BuiltinNeiLoadingSupport implements INeiLoadingSupport {

    private static final String PROVIDER = "builtin-nei";

    @Override
    public boolean supports(NeiHandlerEntry entry) {
        return key(entry) != null;
    }

    @Override
    public NeiLoadingResult load(NeiHandlerEntry entry) {
        String key = key(entry);
        if (key == null) {
            return NeiLoadingResult.unsupported();
        }
        if (!(entry.getHandler() instanceof ICraftingHandler handler)) {
            return NeiLoadingResult.error(NeiLoadingSource.CORE_NEI_BUILTIN, PROVIDER, key, "handler is not craftable");
        }
        try {
            IRecipeHandler loaded = handler.getRecipeHandler(key);
            if (loaded == null) {
                return NeiLoadingResult
                    .error(NeiLoadingSource.CORE_NEI_BUILTIN, PROVIDER, key, "loaded handler is null");
            }
            return NeiLoadingResult.loaded(loaded, NeiLoadingSource.CORE_NEI_BUILTIN, PROVIDER, key);
        } catch (Throwable t) {
            return NeiLoadingResult.error(NeiLoadingSource.CORE_NEI_BUILTIN, PROVIDER, key, errorReason(t));
        }
    }

    private static String key(NeiHandlerEntry entry) {
        Class<?> handlerClass = entry.getHandler()
            .getClass();
        if (handlerClass == ShapedRecipeHandler.class || handlerClass == ShapelessRecipeHandler.class
            || handlerClass == FireworkRecipeHandler.class) {
            return "crafting";
        }
        if (handlerClass == FurnaceRecipeHandler.class) {
            return "smelting";
        }
        if (handlerClass == BrewingRecipeHandler.class) {
            return "brewing";
        }
        if (handlerClass == FuelRecipeHandler.class) {
            return "fuel";
        }
        return null;
    }

    private static String errorReason(Throwable t) {
        return t.getClass()
            .getSimpleName() + (t.getMessage() == null ? "" : ": " + t.getMessage());
    }
}
