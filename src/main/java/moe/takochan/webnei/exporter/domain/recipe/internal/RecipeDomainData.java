package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

import codechicken.nei.drawable.DrawableResource;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.recipe.RecipeExportModel;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryCatalystRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryRow;
import moe.takochan.webnei.exporter.engine.store.IDomainData;

/**
 * recipe domain store 的内部结果集。
 *
 * <p>
 * 该类只持有分类与 catalyst 结果集；注册编排职责由 RecipeRegistrar 负责。
 */
public final class RecipeDomainData implements IDomainData {

    private final String datasetId;

    /**
     * 按 handler key 去重保存分类身份，并保持 NEI 扫描顺序。
     *
     * <p>
     * key 是 handler class + handler id + overlay id 的稳定组合，不写入表，只用于 exporter 内部识别同一个 NEI handler。
     */
    private final Map<String, RecipeCategoryIdentity> identitiesByHandlerKey = new LinkedHashMap<>();

    /** catalyst 行按 category_id + item_variant_id 去重。 */
    private final Map<String, RecipeCategoryCatalystRow> catalysts = new LinkedHashMap<>();

    public RecipeDomainData(String datasetId) {
        this.datasetId = datasetId;
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
            if (hasIconStack(identity)) {
                ItemStack copy = identity.getIconStack()
                    .copy();
                copy.stackSize = 1;
                out.put(identity.getCategoryId(), copy);
            }
        }
        return out;
    }

    /**
     * 返回无可用 ItemStack、但带 NEI 自绘贴图的分类的 category id 到 {@link DrawableResource} 映射。
     *
     * <p>
     * 图标来源优先级为 ItemStack &gt; 贴图 &gt; 文字，因此这里跳过已有 ItemStack 的分类。
     */
    public Map<String, DrawableResource> categoryIconImages() {
        Map<String, DrawableResource> out = new LinkedHashMap<>();
        for (RecipeCategoryIdentity identity : identitiesByHandlerKey.values()) {
            if (!hasIconStack(identity) && identity.getIconImage() != null) {
                out.put(identity.getCategoryId(), identity.getIconImage());
            }
        }
        return out;
    }

    /**
     * 返回既无 ItemStack 也无贴图的分类的 category id 到文字兜底映射。
     *
     * <p>
     * 兜底取 display_name 的前两个字符，模仿 NEI tab 在无图标时的显示，确保每个分类都有可渲染的图标。
     */
    public Map<String, String> categoryIconTexts() {
        Map<String, String> out = new LinkedHashMap<>();
        for (RecipeCategoryIdentity identity : identitiesByHandlerKey.values()) {
            if (!hasIconStack(identity) && identity.getIconImage() == null) {
                out.put(identity.getCategoryId(), fallbackText(identity.getDisplayName()));
            }
        }
        return out;
    }

    boolean putIdentity(RecipeCategoryIdentity identity) {
        return identitiesByHandlerKey.putIfAbsent(identity.getHandlerKey(), identity) == null;
    }

    void putCatalyst(RecipeCategoryCatalystRow row) {
        catalysts.putIfAbsent(row.getCategoryId() + '\u0000' + row.getItemVariantId(), row);
    }

    private static boolean hasIconStack(RecipeCategoryIdentity identity) {
        return identity.getIconStack() != null && identity.getIconStack()
            .getItem() != null;
    }

    private static String fallbackText(String displayName) {
        String text = displayName == null || displayName.isEmpty() ? "??" : displayName;
        return text.length() > 2 ? text.substring(0, 2) : text;
    }

    /**
     * 生成 recipe domain 的导出模型。
     *
     * <p>
     * 输出 recipe_category 行（dataset_id、category_id、display_name、mod_id）以及 recipe_category_catalyst 行
     * （能打开该分类的物品 + 展示顺序）。
     */
    @Override
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
