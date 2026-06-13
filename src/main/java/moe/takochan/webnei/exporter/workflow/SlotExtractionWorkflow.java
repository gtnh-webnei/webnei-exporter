package moe.takochan.webnei.exporter.workflow;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
import moe.takochan.webnei.exporter.model.ExportDataset;
import moe.takochan.webnei.exporter.model.ExportRow;
import moe.takochan.webnei.exporter.model.ExportSection;
import moe.takochan.webnei.exporter.nei.recipe.ExtractedCandidate;
import moe.takochan.webnei.exporter.nei.recipe.ExtractedHandler;
import moe.takochan.webnei.exporter.nei.recipe.ExtractedRecipe;
import moe.takochan.webnei.exporter.nei.recipe.ExtractedStack;
import moe.takochan.webnei.exporter.nei.recipe.SlotExtraction;
import moe.takochan.webnei.exporter.nei.recipe.StandardSlotExtractor;
import moe.takochan.webnei.exporter.nei.scan.NeiHandlerDescriptor;
import moe.takochan.webnei.exporter.nei.scan.NeiHandlerScanner;

/** Builds a standard NEI slot extraction dataset and delegates output to the configured bundle writer. */
public final class SlotExtractionWorkflow {

    private static final String DATASET_NAME = "slot-extraction";

    private final NeiHandlerScanner scanner;
    private final StandardSlotExtractor extractor;
    private final IBundleWriter bundleWriter;

    public SlotExtractionWorkflow() {
        this(new NeiHandlerScanner(), new StandardSlotExtractor(), new TsvBundleWriter());
    }

    SlotExtractionWorkflow(NeiHandlerScanner scanner, StandardSlotExtractor extractor, IBundleWriter bundleWriter) {
        this.scanner = scanner;
        this.extractor = extractor;
        this.bundleWriter = bundleWriter;
    }

    public BundleResult run() {
        try {
            ExportDataset dataset = buildDataset(extractor.extract(scanner.scanEntries()));
            return bundleWriter.write(dataset, defaultTarget(), BundleContext.defaults());
        } catch (BundleException e) {
            WebneiExporterMod.LOG.error("Failed to write WebNEI slot extraction bundle", e);
            return BundleResult.failure(BundleFormat.TSV, e.getMessage());
        } catch (RuntimeException e) {
            WebneiExporterMod.LOG.error("Failed to extract WebNEI slots", e);
            return BundleResult.failure(BundleFormat.TSV, e.getMessage());
        }
    }

    private static BundleTarget defaultTarget() {
        File outputDirectory = new File(Minecraft.getMinecraft().mcDataDir, "webnei-exporter/bundles");
        return BundleTarget.directory(outputDirectory);
    }

    private static ExportDataset buildDataset(SlotExtraction extraction) {
        List<ExportSection> sections = new ArrayList<>();
        sections.add(new ExportSection("handlers", handlerColumns(), handlerRows(extraction.handlers)));
        sections.add(new ExportSection("recipes", recipeColumns(), recipeRows(extraction.recipes)));
        sections.add(new ExportSection("stacks", stackColumns(), stackRows(extraction.stacks)));
        sections.add(new ExportSection("candidates", candidateColumns(), candidateRows(extraction.candidates)));
        return new ExportDataset(DATASET_NAME, sections);
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
