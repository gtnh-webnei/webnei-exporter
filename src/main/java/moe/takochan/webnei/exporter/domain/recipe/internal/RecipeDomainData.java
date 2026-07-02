package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

import codechicken.nei.drawable.DrawableResource;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.recipe.RecipeExportModel;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryCatalystRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCategoryRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeSlotCandidateRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeSlotLayoutRow;
import moe.takochan.webnei.exporter.engine.store.IDomainData;
import moe.takochan.webnei.exporter.util.StableHash;

/**
 * recipe domain store 的内部结果集。
 *
 * <p>
 * 持有分类身份、catalyst、recipe、slot layout、slot candidate 行集合，以及它们的稳定 ID 与顺序分配状态。
 */
public final class RecipeDomainData implements IDomainData {

    /** 输入格 role。 */
    static final String ROLE_INPUT = "input";

    /** 输出格 role。 */
    static final String ROLE_OUTPUT = "output";

    /** 辅助格 role（有主输出时 NEI other 的归类）。 */
    static final String ROLE_AUXILIARY = "auxiliary";

    /** NEI item 格子 hitbox 边长；layout 表 width/height 使用。 */
    private static final int SLOT_SIZE = 18;

    /** 默认候选概率，单位为 1。 */
    private static final double DEFAULT_PROBABILITY = 1.0;

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

    /** recipe 行按 recipe_id 去重，保持 NEI 顺序。 */
    private final Map<String, RecipeRow> recipes = new LinkedHashMap<>();

    /** slot layout 行按 (category_id, slot_key) 去重；同 category 下相同 role + x + y 复用同一 slot。 */
    private final Map<String, RecipeSlotLayoutRow> slotLayouts = new LinkedHashMap<>();

    /** candidate 行按 (recipe_id, slot_key, candidate_order) 去重。 */
    private final Map<String, RecipeSlotCandidateRow> slotCandidates = new LinkedHashMap<>();

    /** 每个 category 的 slot layout display_order 递增计数。 */
    private final Map<String, Integer> nextSlotDisplayOrderByCategory = new LinkedHashMap<>();

    /** 每个 category 的 recipe display_order 递增计数，保持 NEI 扫描顺序。 */
    private final Map<String, Integer> nextRecipeDisplayOrderByCategory = new LinkedHashMap<>();

    /** recipe ID prefix 与 hash 的分隔符。 */
    private static final char RECIPE_ID_HASH_SEPARATOR = '@';

    /** recipe ID occurrence 后缀分隔符。 */
    private static final char RECIPE_ID_OCCURRENCE_SEPARATOR = '.';

    /** recipe ID 稳定哈希输入的内部键分隔符。 */
    private static final char RECIPE_ID_KEY_SEPARATOR = '\u0000';

    /** 相同 base recipe ID 的出现次数，用于在罕见碰撞时为 recipe_id 追加 occurrence。 */
    private final Map<String, Integer> recipeIdOccurrenceCounts = new LinkedHashMap<>();

    public RecipeDomainData(String datasetId) {
        this.datasetId = datasetId;
    }

    /** category 图标 ItemStack 映射，供 asset domain 渲染。 */
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

    /** category 自绘贴图映射。 */
    public Map<String, DrawableResource> categoryIconImages() {
        Map<String, DrawableResource> out = new LinkedHashMap<>();
        for (RecipeCategoryIdentity identity : identitiesByHandlerKey.values()) {
            if (!hasIconStack(identity) && identity.getIconImage() != null) {
                out.put(identity.getCategoryId(), identity.getIconImage());
            }
        }
        return out;
    }

    /** category 文字兜底映射；既无 ItemStack 也无贴图时取 display_name 前两字。 */
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

    /**
     * 注册一个配方页面的 visual facts。
     *
     * <p>
     * 内部统一分配 recipe_id、slot_key、display_order、candidate_order，避免散落在采集器或 registrar。
     */
    void registerVisual(RecipeCategoryIdentity identity, RecipeVisualObservation observation) {
        String categoryId = identity.getCategoryId();
        String recipeId = nextRecipeId(identity, observation);
        int recipeDisplayOrder = nextRecipeDisplayOrder(categoryId);
        if (recipes.putIfAbsent(recipeId, new RecipeRow(datasetId, recipeId, categoryId, recipeDisplayOrder)) != null) {
            return;
        }
        boolean hasResult = observation.getResult() != null;
        registerSlots(categoryId, recipeId, ROLE_INPUT, observation.getInputs());
        registerSlots(categoryId, recipeId, ROLE_INPUT, observation.getExtraInputs());
        if (hasResult) {
            registerSlots(categoryId, recipeId, ROLE_OUTPUT, Collections.singletonList(observation.getResult()));
            registerSlots(categoryId, recipeId, ROLE_AUXILIARY, observation.getOthers());
        } else {
            registerSlots(categoryId, recipeId, ROLE_OUTPUT, observation.getOthers());
        }
        registerSlots(categoryId, recipeId, ROLE_OUTPUT, observation.getExtraOutputs());
    }

    private void registerSlots(String categoryId, String recipeId, String role, List<RecipeSlotObservation> slots) {
        if (slots == null || slots.isEmpty()) {
            return;
        }
        for (RecipeSlotObservation slot : slots) {
            String slotKey = slotKey(role, slot.getX(), slot.getY());
            registerSlotLayout(categoryId, role, slotKey, slot.getX(), slot.getY());
            registerCandidates(recipeId, slotKey, slot);
        }
    }

    private void registerSlotLayout(String categoryId, String role, String slotKey, int x, int y) {
        String layoutKey = categoryId + '\u0000' + slotKey;
        if (slotLayouts.containsKey(layoutKey)) {
            return;
        }
        int displayOrder = nextSlotDisplayOrderByCategory.merge(categoryId, 1, Integer::sum) - 1;
        slotLayouts.put(
            layoutKey,
            new RecipeSlotLayoutRow(datasetId, categoryId, slotKey, role, x, y, SLOT_SIZE, SLOT_SIZE, displayOrder));
    }

    private void registerCandidates(String recipeId, String slotKey, RecipeSlotObservation slot) {
        int order = 0;
        for (RecipeCandidateObservation candidate : slot.getCandidates()) {
            String candidateKey = recipeId + '\u0000' + slotKey + '\u0000' + order;
            slotCandidates.putIfAbsent(
                candidateKey,
                new RecipeSlotCandidateRow(
                    datasetId,
                    recipeId,
                    slotKey,
                    order,
                    candidate.getTargetDomain(),
                    candidate.getTargetId(),
                    candidate.getAmount(),
                    DEFAULT_PROBABILITY));
            order++;
        }
    }

    private String nextRecipeId(RecipeCategoryIdentity identity, RecipeVisualObservation observation) {
        String hashInput = identity.getCategoryId() + RECIPE_ID_KEY_SEPARATOR + visualFingerprint(observation);
        String baseRecipeId = identity.getRecipeIdPrefix() + RECIPE_ID_HASH_SEPARATOR + StableHash.shortHash(hashInput);
        int occurrence = recipeIdOccurrenceCounts.merge(baseRecipeId, 1, Integer::sum);
        return occurrence == 1 ? baseRecipeId : baseRecipeId + RECIPE_ID_OCCURRENCE_SEPARATOR + occurrence;
    }

    private int nextRecipeDisplayOrder(String categoryId) {
        return nextRecipeDisplayOrderByCategory.merge(categoryId, 1, Integer::sum) - 1;
    }

    /** 把一次 observation 的 slots + candidates 序列化成稳定指纹，用于派生 recipe_id。 */
    private static String visualFingerprint(RecipeVisualObservation observation) {
        StringBuilder out = new StringBuilder();
        appendSlots(out, "i", observation.getInputs());
        if (observation.getResult() != null) {
            appendSlots(out, "r", Collections.singletonList(observation.getResult()));
        }
        appendSlots(out, "o", observation.getOthers());
        appendSlots(out, "xi", observation.getExtraInputs());
        appendSlots(out, "xo", observation.getExtraOutputs());
        return out.toString();
    }

    private static void appendSlots(StringBuilder out, String tag, List<RecipeSlotObservation> slots) {
        for (RecipeSlotObservation slot : slots) {
            out.append(tag)
                .append('@')
                .append(slot.getX())
                .append(',')
                .append(slot.getY())
                .append('[');
            for (RecipeCandidateObservation candidate : slot.getCandidates()) {
                out.append(candidate.getTargetDomain())
                    .append(':')
                    .append(candidate.getTargetId())
                    .append('x')
                    .append(candidate.getAmount())
                    .append(';');
            }
            out.append(']');
        }
        out.append('|');
    }

    /** slot_key 由 role + 坐标派生，category 内稳定。 */
    private static String slotKey(String role, int x, int y) {
        return role + '@' + x + ',' + y;
    }

    private static boolean hasIconStack(RecipeCategoryIdentity identity) {
        return identity.getIconStack() != null && identity.getIconStack()
            .getItem() != null;
    }

    private static String fallbackText(String displayName) {
        String text = displayName == null || displayName.isEmpty() ? "??" : displayName;
        return text.length() > 2 ? text.substring(0, 2) : text;
    }

    @Override
    public IExportModel toExportModel() {
        List<RecipeCategoryRow> categories = new ArrayList<>();
        for (RecipeCategoryIdentity identity : identitiesByHandlerKey.values()) {
            categories.add(
                new RecipeCategoryRow(
                    datasetId,
                    identity.getCategoryId(),
                    identity.getDisplayName(),
                    identity.getModId(),
                    identity.getCanvasWidth(),
                    identity.getCanvasHeight()));
        }
        return new RecipeExportModel(
            categories,
            new ArrayList<>(catalysts.values()),
            new ArrayList<>(recipes.values()),
            new ArrayList<>(slotLayouts.values()),
            new ArrayList<>(slotCandidates.values()));
    }
}
