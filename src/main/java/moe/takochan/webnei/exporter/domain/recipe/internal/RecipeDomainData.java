package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

import codechicken.nei.recipe.IRecipeHandler;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.recipe.RecipeExportModel;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeCategoryCandidate;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeCategoryHookRegistry;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryRow;

public final class RecipeDomainData {

    private final String datasetId;

    private final RecipeCategoryIdentityResolver identityResolver = new RecipeCategoryIdentityResolver();

    private final RecipeCategoryHookRegistry recipeCategoryHooks = new RecipeCategoryHookRegistry();

    /**
     * 按 handler key 去重保存分类身份，并保持 NEI 扫描顺序。
     *
     * <p>
     * key 是 handler class + handler id + overlay id 的稳定组合，不写入表，只用于 exporter 内部识别同一个 NEI handler。
     */
    private final Map<String, RecipeCategoryIdentity> identitiesByHandlerKey = new LinkedHashMap<>();

    public RecipeDomainData(String datasetId) {
        this.datasetId = datasetId;
    }

    /**
     * 把一个 NEI recipe handler 纳入 recipe domain；当前解析为分类。
     *
     * <p>
     * 本方法只做 handler 级去重；category id 的撞名消歧必须等所有 handler 都收集完成后统一处理，否则先注册的分类无法知道后面是否会撞名。
     */
    public void register(IRecipeHandler handler) {
        RecipeCategoryIdentity identity = identityResolver.resolve(handler);
        RecipeCategoryCandidate candidate = new RecipeCategoryCandidate(
            identity.getBaseCategoryId(),
            identity.getModId(),
            identity.getDisplayName());
        if (recipeCategoryHooks.shouldSkip(candidate)) {
            return;
        }
        identitiesByHandlerKey.putIfAbsent(identity.getHandlerKey(), identity);
    }

    /**
     * 返回最终 category id 到分类图标原始 {@link ItemStack} 的映射，供 asset domain 注册渲染任务。
     *
     * <p>
     * asset 表使用最终 category id 作为 owner_id，因此这里先调用 {@link #resolveCategoryIds()} 得到 handler key 到最终 category id
     * 的映射，再把内部保存的 icon stack 转成 category id 维度。返回前复制 ItemStack，避免后续渲染修改原始运行时对象。
     */
    public Map<String, ItemStack> categoryIconStacks() {
        Map<String, ItemStack> out = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : resolveCategoryIds().entrySet()) {
            ItemStack iconStack = identitiesByHandlerKey.get(entry.getKey())
                .getIconStack();
            if (iconStack != null && iconStack.getItem() != null) {
                ItemStack copy = iconStack.copy();
                copy.stackSize = 1;
                out.put(entry.getValue(), copy);
            }
        }
        return out;
    }

    /**
     * 生成 recipe domain 的导出模型。
     *
     * <p>
     * 输出行只包含当前 recipe_category 表合同：dataset_id、category_id、display_name、mod_id。
     */
    public IExportModel toExportModel() {
        List<RecipeCategoryRow> rows = new ArrayList<>();
        Map<String, String> categoryIds = resolveCategoryIds();
        for (RecipeCategoryIdentity identity : identitiesByHandlerKey.values()) {
            rows.add(
                new RecipeCategoryRow(
                    datasetId,
                    categoryIds.get(identity.getHandlerKey()),
                    identity.getDisplayName(),
                    identity.getModId()));
        }
        return new RecipeExportModel(rows);
    }

    /**
     * 一次性把所有 handler 的基础 category id 消歧为唯一的最终 category id。
     *
     * <p>
     * 流程分两步：
     * <ol>
     * <li>先统计每个 baseCategoryId 出现次数；只出现一次的可以原样使用。
     * <li>出现多次时追加 handler class 的简短片段，例如 {@code gregtech:machine.GTNEIDefaultHandler}。
     * </ol>
     * 如果追加 class 片段后仍然撞名，再追加数字后缀，保证最终 category id 在同一 dataset 内唯一。
     *
     * @return handler key 到最终 category id 的映射。handler key 不导出，只在 exporter 内部用于把运行时 handler 绑定到最终分类。
     */
    private Map<String, String> resolveCategoryIds() {
        Map<String, Integer> baseCounts = new HashMap<>();
        for (RecipeCategoryIdentity identity : identitiesByHandlerKey.values()) {
            baseCounts.merge(identity.getBaseCategoryId(), 1, Integer::sum);
        }

        Map<String, String> categoryIds = new LinkedHashMap<>();
        Map<String, Integer> usedCounts = new HashMap<>();
        for (RecipeCategoryIdentity identity : identitiesByHandlerKey.values()) {
            String categoryId = baseCounts.get(identity.getBaseCategoryId()) > 1
                ? identity.getBaseCategoryId() + "." + identity.getDisambiguationSegment()
                : identity.getBaseCategoryId();
            int used = usedCounts.merge(categoryId, 1, Integer::sum);
            if (used > 1) {
                categoryId = categoryId + "." + (used - 1);
            }
            categoryIds.put(identity.getHandlerKey(), categoryId);
        }
        return categoryIds;
    }
}
