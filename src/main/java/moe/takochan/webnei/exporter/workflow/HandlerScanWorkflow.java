package moe.takochan.webnei.exporter.workflow;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;

import moe.takochan.webnei.exporter.WebneiExporterMod;
import moe.takochan.webnei.exporter.bundle.BundleContext;
import moe.takochan.webnei.exporter.bundle.BundleException;
import moe.takochan.webnei.exporter.bundle.BundleFormat;
import moe.takochan.webnei.exporter.bundle.BundleResult;
import moe.takochan.webnei.exporter.bundle.BundleTarget;
import moe.takochan.webnei.exporter.bundle.IBundleWriter;
import moe.takochan.webnei.exporter.bundle.tsv.TsvBundleWriter;
import moe.takochan.webnei.exporter.export.ExportExecutionContext;
import moe.takochan.webnei.exporter.export.IExportWorkflow;
import moe.takochan.webnei.exporter.model.ExportDataset;
import moe.takochan.webnei.exporter.model.ExportRow;
import moe.takochan.webnei.exporter.model.ExportSection;
import moe.takochan.webnei.exporter.nei.scan.NeiHandlerDescriptor;
import moe.takochan.webnei.exporter.nei.scan.NeiHandlerScanner;

/** Builds the handler-scan dataset and delegates output to the configured bundle writer. */
public final class HandlerScanWorkflow implements IExportWorkflow {

    public static final String ID = "handler-scan";
    private static final String DATASET_NAME = ID;
    private static final String SECTION_NAME = "nei-handlers";

    private final NeiHandlerScanner scanner;
    private final IBundleWriter bundleWriter;

    public HandlerScanWorkflow() {
        this(new NeiHandlerScanner(), new TsvBundleWriter());
    }

    HandlerScanWorkflow(NeiHandlerScanner scanner, IBundleWriter bundleWriter) {
        this.scanner = scanner;
        this.bundleWriter = bundleWriter;
    }

    @Override
    public String id() {
        return DATASET_NAME;
    }

    @Override
    public String labelKey() {
        return "webnei.task.handlers";
    }

    @Override
    public BundleResult execute(ExportExecutionContext context) {
        try {
            ExportDataset dataset = buildDataset(scanner.scan());
            return bundleWriter.write(dataset, defaultTarget(), BundleContext.defaults());
        } catch (BundleException e) {
            WebneiExporterMod.LOG.error("Failed to write WebNEI handler scan bundle", e);
            return BundleResult.failure(BundleFormat.TSV, e.getMessage());
        }
    }

    private static BundleTarget defaultTarget() {
        File outputDirectory = new File(Minecraft.getMinecraft().mcDataDir, "webnei-exporter/bundles");
        return BundleTarget.directory(outputDirectory);
    }

    private static ExportDataset buildDataset(List<NeiHandlerDescriptor> handlers) {
        List<ExportRow> rows = new ArrayList<>();
        for (NeiHandlerDescriptor handler : handlers) {
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
        return new ExportDataset(
            DATASET_NAME,
            Collections.singletonList(new ExportSection(SECTION_NAME, columns(), rows)));
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
