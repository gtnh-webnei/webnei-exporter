package moe.takochan.webnei.exporter.step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import moe.takochan.webnei.exporter.model.ExportRow;
import moe.takochan.webnei.exporter.model.ExportSection;
import moe.takochan.webnei.exporter.nei.scan.NeiHandlerDescriptor;
import moe.takochan.webnei.exporter.nei.scan.NeiHandlerEntry;
import moe.takochan.webnei.exporter.nei.scan.NeiHandlerScanner;

/**
 * NEI handler/category 发现步骤。
 *
 * <p>本 step 只负责“当前 NEI 注册了哪些 handler，以及这些 handler 暴露了哪些基础信息”。
 * 它不加载 recipes，不抽 slots，也不决定最终 bundle 如何落库。
 */
public final class HandlerExportStep implements IExportStep {

    public static final String ID = "handler-export";

    /** 后续 step 通过这个 key 复用已扫描的 handler entries，避免重复扫描。 */
    public static final String HANDLER_ENTRIES_KEY = "nei.handler.entries";

    private static final String SECTION_NAME = "nei-handlers";

    private final NeiHandlerScanner scanner;

    public HandlerExportStep() {
        this(new NeiHandlerScanner());
    }

    HandlerExportStep(NeiHandlerScanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String labelKey() {
        return "webnei.task.handlers";
    }

    @Override
    public void execute(ExportStepContext context) {
        List<NeiHandlerEntry> entries = scanner.scanEntries();
        context.put(HANDLER_ENTRIES_KEY, entries);
        context.addSection(new ExportSection(SECTION_NAME, columns(), rows(entries)));
    }

    /** 将 NEI handler 描述转换成通用 section row，具体 TSV 写出由 bundle 层处理。 */
    private static List<ExportRow> rows(List<NeiHandlerEntry> entries) {
        List<ExportRow> rows = new ArrayList<>();
        for (NeiHandlerEntry entry : entries) {
            NeiHandlerDescriptor handler = entry.descriptor;
            rows.add(
                ExportRow.of(
                    Integer.toString(handler.registrationIndex),
                    handler.sourceList,
                    handler.stableKey,
                    handler.handlerClass,
                    handler.handlerId,
                    handler.overlayId,
                    handler.recipeName,
                    handler.recipeTabName,
                    handler.resolvedCategoryId,
                    handler.modId,
                    handler.modName,
                    handler.iconStackId,
                    handler.catalystKey,
                    Integer.toString(handler.loadedRecipeCount),
                    handler.extractionStatus,
                    handler.reason));
        }
        return rows;
    }

    private static List<String> columns() {
        return Arrays.asList(
            "registration_index",
            "source_list",
            "stable_handler_key",
            "handler_class",
            "handler_id",
            "overlay_id",
            "recipe_name",
            "recipe_tab_name",
            "resolved_category_id",
            "mod_id",
            "mod_name",
            "icon_stack_id",
            "catalyst_key",
            "loaded_recipe_count",
            "extraction_status",
            "reason");
    }
}
