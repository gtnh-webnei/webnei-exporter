package moe.takochan.webnei.exporter.step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import moe.takochan.webnei.exporter.model.ExportRow;
import moe.takochan.webnei.exporter.model.ExportSection;
import moe.takochan.webnei.exporter.nei.recipe.ExtractedCandidate;
import moe.takochan.webnei.exporter.nei.recipe.ExtractedHandler;
import moe.takochan.webnei.exporter.nei.recipe.ExtractedRecipe;
import moe.takochan.webnei.exporter.nei.recipe.ExtractedStack;
import moe.takochan.webnei.exporter.nei.recipe.SlotExtraction;
import moe.takochan.webnei.exporter.nei.recipe.StandardSlotExtractor;
import moe.takochan.webnei.exporter.nei.scan.NeiHandlerDescriptor;
import moe.takochan.webnei.exporter.nei.scan.NeiHandlerEntry;
import moe.takochan.webnei.exporter.nei.scan.NeiHandlerScanner;

/**
 * recipe visual facts 验证导出步骤。
 *
 * <p>本 step 服务当前验证：确认能否从 NEI final result 中拿到 recipe 顺序、slot 坐标和候选栈。
 * 它不是长期的“slots 功能”，后续会被正式 recipe 导出 step 替代或吸收。
 */
public final class RecipeVisualFactsExportStep implements IExportStep {

    public static final String ID = "recipe-visual-facts";

    private final NeiHandlerScanner scanner;
    private final StandardSlotExtractor extractor;

    public RecipeVisualFactsExportStep() {
        this(new NeiHandlerScanner(), new StandardSlotExtractor());
    }

    RecipeVisualFactsExportStep(NeiHandlerScanner scanner, StandardSlotExtractor extractor) {
        this.scanner = scanner;
        this.extractor = extractor;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String labelKey() {
        return "webnei.task.slots";
    }

    @Override
    public void execute(ExportStepContext context) {
        SlotExtraction extraction = extractor.extract(handlerEntries(context));
        context.addSection(new ExportSection("handlers", handlerColumns(), handlerRows(extraction.handlers)));
        context.addSection(new ExportSection("recipes", recipeColumns(), recipeRows(extraction.recipes)));
        context.addSection(new ExportSection("stacks", stackColumns(), stackRows(extraction.stacks)));
        context.addSection(new ExportSection("candidates", candidateColumns(), candidateRows(extraction.candidates)));
    }

    /** 优先复用 HandlerExportStep 的扫描结果；单独运行时才自行扫描。 */
    @SuppressWarnings("unchecked")
    private List<NeiHandlerEntry> handlerEntries(ExportStepContext context) {
        Object value = context.get(HandlerExportStep.HANDLER_ENTRIES_KEY);
        if (value instanceof List) {
            return (List<NeiHandlerEntry>) value;
        }
        return scanner.scanEntries();
    }

    private static List<ExportRow> handlerRows(List<ExtractedHandler> handlers) {
        List<ExportRow> rows = new ArrayList<>();
        for (ExtractedHandler handler : handlers) {
            NeiHandlerDescriptor descriptor = handler.descriptor;
            rows.add(
                ExportRow.of(
                    descriptor.stableKey,
                    descriptor.handlerClass,
                    descriptor.resolvedCategoryId,
                    descriptor.sourceList,
                    descriptor.handlerId,
                    descriptor.overlayId,
                    descriptor.recipeName,
                    descriptor.recipeTabName,
                    descriptor.modId,
                    descriptor.modName,
                    Integer.toString(handler.recipeCount),
                    handler.status,
                    handler.reason));
        }
        return rows;
    }

    private static List<ExportRow> recipeRows(List<ExtractedRecipe> recipes) {
        List<ExportRow> rows = new ArrayList<>();
        for (ExtractedRecipe recipe : recipes) {
            rows.add(
                ExportRow.of(
                    recipe.handlerKey,
                    Integer.toString(recipe.recipeIndex),
                    recipe.fingerprint,
                    Integer.toString(recipe.ingredientCount),
                    Integer.toString(recipe.resultCount),
                    Integer.toString(recipe.otherCount),
                    recipe.status,
                    recipe.reason));
        }
        return rows;
    }

    private static List<ExportRow> stackRows(List<ExtractedStack> stacks) {
        List<ExportRow> rows = new ArrayList<>();
        for (ExtractedStack stack : stacks) {
            rows.add(
                ExportRow.of(
                    stack.handlerKey,
                    Integer.toString(stack.recipeIndex),
                    stack.stackSource,
                    Integer.toString(stack.stackIndex),
                    Integer.toString(stack.x),
                    Integer.toString(stack.y),
                    Integer.toString(stack.candidateCount),
                    stack.firstCandidate,
                    stack.status,
                    stack.reason));
        }
        return rows;
    }

    private static List<ExportRow> candidateRows(List<ExtractedCandidate> candidates) {
        List<ExportRow> rows = new ArrayList<>();
        for (ExtractedCandidate candidate : candidates) {
            rows.add(
                ExportRow.of(
                    candidate.handlerKey,
                    Integer.toString(candidate.recipeIndex),
                    candidate.stackSource,
                    Integer.toString(candidate.stackIndex),
                    Integer.toString(candidate.candidateIndex),
                    candidate.itemId,
                    Integer.toString(candidate.damage),
                    Integer.toString(candidate.stackSize),
                    candidate.displayName));
        }
        return rows;
    }

    private static List<String> handlerColumns() {
        return Arrays.asList(
            "handler_key",
            "handler_class",
            "category_id",
            "source_list",
            "handler_id",
            "overlay_id",
            "recipe_name",
            "recipe_tab_name",
            "mod_id",
            "mod_name",
            "recipe_count",
            "status",
            "reason");
    }

    private static List<String> recipeColumns() {
        return Arrays.asList(
            "handler_key",
            "recipe_index",
            "recipe_fingerprint",
            "ingredient_count",
            "result_count",
            "other_count",
            "status",
            "reason");
    }

    private static List<String> stackColumns() {
        return Arrays.asList(
            "handler_key",
            "recipe_index",
            "stack_source",
            "stack_index",
            "x",
            "y",
            "candidate_count",
            "first_candidate",
            "status",
            "reason");
    }

    private static List<String> candidateColumns() {
        return Arrays.asList(
            "handler_key",
            "recipe_index",
            "stack_source",
            "stack_index",
            "candidate_index",
            "item_id",
            "damage",
            "stack_size",
            "display_name");
    }
}
