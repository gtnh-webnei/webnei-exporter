package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.item.store.ItemDomainStore;
import moe.takochan.webnei.exporter.domain.recipe.RecipeExportModel;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeCategoryCandidate;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeCategoryHookRegistry;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryCatalystRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryRow;

/**
 * recipe domain store 的内部数据和注册逻辑。
 *
 * <p>
 * 该类维护分类身份去重，并挂载 catalyst collector；catalyst 行的 item variant 由 collector 通过 item store 解析。
 * 跨 domain API 由 RecipeDomainStore 包装后暴露。
 */
public final class RecipeDomainData {

    private final String datasetId;
    private final RecipeCategoryIdentityResolver identityResolver = new RecipeCategoryIdentityResolver();
    private final RecipeCategoryHookRegistry recipeCategoryHooks = new RecipeCategoryHookRegistry();
    private final RecipeCatalystCollector catalystCollector;

    /**
     * 按 handler key 去重保存分类身份，并保持 NEI 扫描顺序。
     *
     * <p>
     * key 是 handler class + handler id + overlay id 的稳定组合，不写入表，只用于 exporter 内部识别同一个 NEI handler。
     */
    private final Map<String, RecipeCategoryIdentity> identitiesByHandlerKey = new LinkedHashMap<>();

    /** catalyst 行按 category_id + item_variant_id 去重。 */
    private final Map<String, RecipeCategoryCatalystRow> catalysts = new LinkedHashMap<>();

    public RecipeDomainData(String datasetId, ItemDomainStore itemStore) {
        this.datasetId = datasetId;
        this.catalystCollector = new RecipeCatalystCollector(itemStore);
    }

    /**
     * 把一个 NEI recipe handler 纳入 recipe domain；解析为分类并挂接其 catalyst。
     *
     * <p>
     * 按 handler key 去重。category id 由 handler 身份确定性派生，不存在撞名，无需后置消歧。命中 skip hook 的 handler 直接跳过。
     */
    public void register(IRecipeHandler handler) {
        RecipeCategoryIdentity identity = identityResolver.resolve(handler);
        RecipeCategoryCandidate candidate = new RecipeCategoryCandidate(
            identity.getCategoryId(),
            identity.getModId(),
            identity.getDisplayName());
        if (recipeCategoryHooks.shouldSkip(candidate)) {
            return;
        }
        if (identitiesByHandlerKey.putIfAbsent(identity.getHandlerKey(), identity) == null) {
            addCatalysts(identity.getCategoryId(), handler);
        }
    }

    private void addCatalysts(String categoryId, IRecipeHandler handler) {
        for (RecipeCategoryCatalystRow row : catalystCollector.collect(datasetId, categoryId, handler)) {
            catalysts.putIfAbsent(row.getCategoryId() + '\u0000' + row.getItemVariantId(), row);
        }
    }

    /**
     * 返回 category id 到分类图标原始 {@link ItemStack} 的映射，供 asset domain 注册渲染任务。
     *
     * <p>
     * asset 表使用 category id 作为 owner_id。返回前复制 ItemStack，避免后续渲染修改原始运行时对象。
     */
    public Map<String, ItemStack> categoryIconStacks() {
        Map<String, ItemStack> out = new LinkedHashMap<>();
        for (RecipeCategoryIdentity identity : identitiesByHandlerKey.values()) {
            ItemStack iconStack = identity.getIconStack();
            if (iconStack != null && iconStack.getItem() != null) {
                ItemStack copy = iconStack.copy();
                copy.stackSize = 1;
                out.put(identity.getCategoryId(), copy);
            }
        }
        return out;
    }

    /**
     * 生成 recipe domain 的导出模型。
     *
     * <p>
     * 输出 recipe_category 行（dataset_id、category_id、display_name、mod_id）以及 recipe_category_catalyst 行
     * （能打开该分类的物品 + 展示顺序）。
     */
    public IExportModel toExportModel() {
        List<RecipeCategoryRow> categories = new ArrayList<>();
        for (RecipeCategoryIdentity identity : identitiesByHandlerKey.values()) {
            categories.add(
                new RecipeCategoryRow(
                    datasetId,
                    identity.getCategoryId(),
                    identity.getDisplayName(),
                    identity.getModId()));
        }
        return new RecipeExportModel(categories, new ArrayList<>(catalysts.values()));
    }
}
