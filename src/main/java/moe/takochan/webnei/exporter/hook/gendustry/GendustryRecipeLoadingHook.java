package moe.takochan.webnei.exporter.hook.gendustry;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bdew.gendustry.nei.ExtractorHandler;
import net.bdew.gendustry.nei.ImprinterHandler;
import net.bdew.gendustry.nei.LiquifierHandler;
import net.bdew.gendustry.nei.MutagenProducerHandler;
import net.bdew.gendustry.nei.MutatronHandler;
import net.bdew.gendustry.nei.ReplicatorHandler;
import net.bdew.gendustry.nei.SamplerHandler;
import net.bdew.gendustry.nei.TransposerHandler;

import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeLoadingHook;

/**
 * 触发 gendustry NEI handler 按 handler 名加载全量配方。
 *
 * <p>
 * gendustry handler 没重写 {@code getOverlayIdentifier()}。但每个 handler 的
 * {@code loadCraftingRecipes(outputId, ...)} 都接受一个等于 handler 名的字符串（{@code "Mutatron"} 等），
 * 命中后会调内部 {@code addAllRecipes()}。直接按类映射这个字符串。
 */
public final class GendustryRecipeLoadingHook implements IRecipeLoadingHook {

    private static final Map<Class<? extends IRecipeHandler>, String> HANDLER_OUTPUT_IDS = buildHandlerOutputIds();

    private static Map<Class<? extends IRecipeHandler>, String> buildHandlerOutputIds() {
        Map<Class<? extends IRecipeHandler>, String> map = new HashMap<>();
        map.put(MutatronHandler.class, "Mutatron");
        map.put(SamplerHandler.class, "Sampler");
        map.put(ImprinterHandler.class, "Imprinter");
        map.put(ExtractorHandler.class, "Extractor");
        map.put(LiquifierHandler.class, "Liquifier");
        map.put(ReplicatorHandler.class, "Replicator");
        map.put(TransposerHandler.class, "Transposer");
        map.put(MutagenProducerHandler.class, "MutagenProducer");
        return map;
    }

    @Override
    public boolean isAvailable() {
        return Mods.GENDUSTRY.isLoaded();
    }

    @Override
    public boolean supports(IRecipeHandler handler) {
        return handler instanceof ICraftingHandler && HANDLER_OUTPUT_IDS.containsKey(handler.getClass());
    }

    @Override
    public List<IRecipeHandler> load(IRecipeHandler handler) {
        String outputId = HANDLER_OUTPUT_IDS.get(handler.getClass());
        if (outputId == null) {
            return Collections.emptyList();
        }
        IRecipeHandler loaded = ((ICraftingHandler) handler).getRecipeHandler(outputId, new Object[0]);
        if (loaded == null || loaded.numRecipes() == 0) {
            return Collections.emptyList();
        }
        return Collections.singletonList(loaded);
    }
}
