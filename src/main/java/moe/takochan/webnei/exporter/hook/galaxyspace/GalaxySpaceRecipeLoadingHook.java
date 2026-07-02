package moe.takochan.webnei.exporter.hook.galaxyspace;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import galaxyspace.core.nei.RocketRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeLoadingHook;

/**
 * 触发 GalaxySpace 火箭 NASA 工作台 handler 按 {@code galacticraft.rocketT<tier>} 全量加载。
 *
 * <p>
 * GalaxySpace 用单一 {@link RocketRecipeHandler} 类承载 T1-T8 八个分类，构造时传 tier，
 * {@code loadCraftingRecipes(String outputId, ...)} 在 outputId 等于 {@code galacticraft.rocketT<tier>}
 * 时遍历自家 recipes push 到 {@code arecipes}；但没重写 {@code getOverlayIdentifier()}，通用 loader 触发不了。
 *
 * <p>
 * handler 的 tier 是私有字段，{@code getRecipeId()} 也私有。但 {@link IRecipeHandler#getHandlerId()} 是 NEI 公开方法，
 * GalaxySpace 实现返回 {@code "galaxyspace.core.nei.rocket.RocketT" + tier + "RecipeHandler"}，从中解析 tier，
 * 拼出 handler 自己识别的 outputId。
 */
public final class GalaxySpaceRecipeLoadingHook implements IRecipeLoadingHook {

    private static final Pattern HANDLER_ID_TIER = Pattern.compile("RocketT(\\d+)RecipeHandler");
    private static final String OUTPUT_ID_PREFIX = "galacticraft.rocketT";

    @Override
    public boolean isAvailable() {
        return Mods.GALAXY_SPACE.isLoaded();
    }

    @Override
    public boolean supports(IRecipeHandler handler) {
        return handler instanceof RocketRecipeHandler && handler instanceof ICraftingHandler;
    }

    @Override
    public List<IRecipeHandler> load(IRecipeHandler handler) {
        String tier = parseTier(handler.getHandlerId());
        if (tier == null) {
            return Collections.emptyList();
        }
        IRecipeHandler loaded = ((ICraftingHandler) handler).getRecipeHandler(OUTPUT_ID_PREFIX + tier, new Object[0]);
        if (loaded == null || loaded.numRecipes() == 0) {
            return Collections.emptyList();
        }
        return Collections.singletonList(loaded);
    }

    private static String parseTier(String handlerId) {
        if (handlerId == null) {
            return null;
        }
        Matcher matcher = HANDLER_ID_TIER.matcher(handlerId);
        return matcher.find() ? matcher.group(1) : null;
    }
}
