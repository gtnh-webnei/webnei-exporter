package moe.takochan.webnei.exporter.bundle.tsv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import moe.takochan.webnei.exporter.bundle.BundleContext;
import moe.takochan.webnei.exporter.bundle.BundleException;
import moe.takochan.webnei.exporter.bundle.BundleFormat;
import moe.takochan.webnei.exporter.bundle.BundleResult;
import moe.takochan.webnei.exporter.bundle.BundleTarget;
import moe.takochan.webnei.exporter.bundle.IBundleWriter;
import moe.takochan.webnei.exporter.domain.ExportModelSet;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.dataset.DatasetExportModel;
import moe.takochan.webnei.exporter.domain.dataset.model.DatasetRow;
import moe.takochan.webnei.exporter.domain.dataset.model.ModRow;
import moe.takochan.webnei.exporter.domain.item.ItemExportModel;
import moe.takochan.webnei.exporter.domain.item.model.ItemRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemToolClassRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.domain.item.model.NeiItemPanelEntryRow;
import moe.takochan.webnei.exporter.domain.nei.recipe.ExtractedCandidate;
import moe.takochan.webnei.exporter.domain.nei.recipe.ExtractedHandler;
import moe.takochan.webnei.exporter.domain.nei.recipe.ExtractedRecipe;
import moe.takochan.webnei.exporter.domain.nei.recipe.ExtractedStack;
import moe.takochan.webnei.exporter.domain.nei.recipe.SlotExtraction;
import moe.takochan.webnei.exporter.domain.nei.scan.NeiHandlerDescriptor;
import moe.takochan.webnei.exporter.domain.nei.scan.NeiHandlerEntry;

/**
 * 把领域中间模型映射成 TSV 文件。
 *
 * <p>
 * step/model 层不包含 columns/row values；所有具体 TSV section 名、列名和字段顺序都集中在这里，避免表格 payload
 * 重新泄漏到领域模型。
 */
public final class TsvBundleWriter implements IBundleWriter {

    @Override
    public BundleFormat format() {
        return BundleFormat.TSV;
    }

    @Override
    public BundleResult write(ExportModelSet models, BundleTarget target, BundleContext context)
        throws BundleException {
        File outputDirectory = new File(target.getOutputDirectory(), outputPath(models));
        if (!outputDirectory.isDirectory() && !outputDirectory.mkdirs()) {
            throw new BundleException("Unable to create bundle directory: " + outputDirectory.getAbsolutePath());
        }

        List<String> files = new ArrayList<>();
        for (TsvSection section : sections(models.getModels())) {
            File file = new File(outputDirectory, section.name + ".tsv");
            writeSection(section, file);
            files.add(file.getAbsolutePath());
        }
        return BundleResult.success(format(), files);
    }

    private static String outputPath(ExportModelSet models) {
        for (IExportModel model : models.getModels()) {
            if (model instanceof DatasetExportModel datasetMod) {
                return datasetOutputPath(datasetMod.getDataset());
            }
        }
        return sanitizePathSegment(models.getPlanId());
    }

    private static String datasetOutputPath(DatasetRow dataset) {
        return sanitizePathSegment(dataset.getPackSlug()) + File.separator
            + sanitizePathSegment(dataset.getPackVersion())
            + File.separator
            + sanitizePathSegment(dataset.getVariant())
            + File.separator
            + sanitizePathSegment(dataset.getLanguage());
    }

    private static String sanitizePathSegment(String value) {
        String sanitized = value.trim()
            .replaceAll("[^A-Za-z0-9._-]", "_");
        sanitized = sanitized.replaceAll("^\\.+", "")
            .replaceAll("\\.+$", "");
        return sanitized.isEmpty() ? "value" : sanitized;
    }

    private static List<TsvSection> sections(List<IExportModel> models) throws BundleException {
        List<TsvSection> sections = new ArrayList<>();
        for (IExportModel model : models) {
            if (model instanceof DatasetExportModel datasetMod) {
                addDatasetModSections(sections, datasetMod);
            } else if (model instanceof ItemExportModel item) {
                addItemSections(sections, item);
            } else if (model instanceof HandlerDiscoveryExportModel handlerDiscovery) {
                sections.add(handlerDiscoverySection(handlerDiscovery));
            } else if (model instanceof RecipeVisualFactsExportModel recipeVisualFacts) {
                addRecipeVisualFactsSections(sections, recipeVisualFacts);
            } else {
                throw new BundleException("No TSV mapper for export model: " + model.type());
            }
        }
        return sections;
    }

    private static void writeSection(TsvSection section, File file) throws BundleException {
        try (TsvRowWriter writer = new TsvRowWriter(file)) {
            writer.writeRow(section.columns);
            for (List<String> row : section.rows) {
                writer.writeRow(row);
            }
        } catch (IOException e) {
            throw new BundleException("Unable to write TSV section: " + file.getAbsolutePath(), e);
        }
    }

    private static void addDatasetModSections(List<TsvSection> sections, DatasetExportModel model) {
        sections.add(datasetSection(model.getDataset()));
        sections.add(modSection(model.getMods()));
    }

    private static TsvSection datasetSection(DatasetRow dataset) {
        List<List<String>> rows = new ArrayList<>();
        rows.add(
            row(
                dataset.getDatasetId(),
                dataset.getPackSlug(),
                dataset.getPackVersion(),
                dataset.getVariant(),
                dataset.getLanguage(),
                dataset.getDisplayName(),
                dataset.getSchemaVersion(),
                dataset.getExporterVersion(),
                dataset.getCreatedAt(),
                dataset.getMinecraftVersion()));
        return new TsvSection(
            "dataset",
            Arrays.asList(
                "dataset_id",
                "pack_slug",
                "pack_version",
                "variant",
                "language",
                "display_name",
                "schema_version",
                "exporter_version",
                "created_at",
                "minecraft_version"),
            rows);
    }

    private static TsvSection modSection(List<ModRow> mods) {
        List<List<String>> rows = new ArrayList<>();
        for (ModRow mod : mods) {
            rows.add(
                row(
                    mod.getDatasetId(),
                    mod.getModId(),
                    mod.getName(),
                    mod.getVersion(),
                    mod.getSourceType(),
                    mod.getSourceFileName(),
                    mod.getSourceSha256(),
                    Boolean.toString(mod.isEnabled())));
        }
        return new TsvSection(
            "mod",
            Arrays.asList(
                "dataset_id",
                "mod_id",
                "name",
                "version",
                "source_type",
                "source_file_name",
                "source_sha256",
                "enabled"),
            rows);
    }

    private static void addItemSections(List<TsvSection> sections, ItemExportModel model) {
        sections.add(itemSection(model.getItems()));
        sections.add(itemVariantSection(model.getVariants()));
        sections.add(itemToolClassSection(model.getToolClasses()));
        sections.add(neiItemPanelEntrySection(model.getPanelEntries()));
    }

    private static TsvSection itemSection(List<ItemRow> items) {
        List<List<String>> rows = new ArrayList<>();
        for (ItemRow item : items) {
            rows.add(
                row(
                    item.getDatasetId(),
                    item.getItemId(),
                    item.getModId(),
                    item.getRegistryName(),
                    item.getUnlocalizedName(),
                    Integer.toString(item.getMaxStackSize()),
                    Integer.toString(item.getMaxDamage()),
                    Integer.toString(item.getRuntimeItemId())));
        }
        return new TsvSection(
            "item",
            Arrays.asList(
                "dataset_id",
                "item_id",
                "mod_id",
                "registry_name",
                "unlocalized_name",
                "max_stack_size",
                "max_damage",
                "runtime_item_id"),
            rows);
    }

    private static TsvSection itemVariantSection(List<ItemVariantRow> variants) {
        List<List<String>> rows = new ArrayList<>();
        for (ItemVariantRow variant : variants) {
            rows.add(
                row(
                    variant.getDatasetId(),
                    variant.getItemVariantId(),
                    variant.getItemId(),
                    Integer.toString(variant.getDamage()),
                    variant.getNbtHash(),
                    variant.getNbtText(),
                    variant.getDisplayName(),
                    variant.getTooltipText(),
                    variant.getChemicalExpression(),
                    variant.getAssetId()));
        }
        return new TsvSection(
            "item_variant",
            Arrays.asList(
                "dataset_id",
                "item_variant_id",
                "item_id",
                "damage",
                "nbt_hash",
                "nbt_text",
                "display_name",
                "tooltip_text",
                "chemical_expression",
                "asset_id"),
            rows);
    }

    private static TsvSection itemToolClassSection(List<ItemToolClassRow> toolClasses) {
        List<List<String>> rows = new ArrayList<>();
        for (ItemToolClassRow toolClass : toolClasses) {
            rows.add(
                row(
                    toolClass.getDatasetId(),
                    toolClass.getItemVariantId(),
                    toolClass.getToolClass(),
                    Integer.toString(toolClass.getHarvestLevel())));
        }
        return new TsvSection(
            "item_tool_class",
            Arrays.asList("dataset_id", "item_variant_id", "tool_class", "harvest_level"),
            rows);
    }

    private static TsvSection neiItemPanelEntrySection(List<NeiItemPanelEntryRow> panelEntries) {
        List<List<String>> rows = new ArrayList<>();
        for (NeiItemPanelEntryRow entry : panelEntries) {
            rows.add(
                row(
                    entry.getDatasetId(),
                    entry.getItemVariantId(),
                    Integer.toString(entry.getPanelIndex()),
                    entry.getCollapsibleCollectionId(),
                    Boolean.toString(entry.isVisibleWhenCollapsed())));
        }
        return new TsvSection(
            "nei_item_panel_entry",
            Arrays.asList(
                "dataset_id",
                "item_variant_id",
                "panel_index",
                "collapsible_collection_id",
                "visible_when_collapsed"),
            rows);
    }

    private static TsvSection handlerDiscoverySection(HandlerDiscoveryExportModel model) {
        List<List<String>> rows = new ArrayList<>();
        for (NeiHandlerEntry entry : model.getEntries()) {
            NeiHandlerDescriptor handler = entry.getDescriptor();
            rows.add(
                row(
                    Integer.toString(handler.getRegistrationIndex()),
                    handler.getSourceList(),
                    handler.getStableKey(),
                    handler.getHandlerClass(),
                    handler.getHandlerId(),
                    handler.getOverlayId(),
                    handler.getRecipeName(),
                    handler.getRecipeTabName(),
                    handler.getResolvedCategoryId(),
                    handler.getModId(),
                    handler.getModName(),
                    handler.getIconStackId(),
                    handler.getCatalystKey(),
                    Integer.toString(handler.getLoadedRecipeCount()),
                    handler.getExtractionStatus(),
                    handler.getReason()));
        }
        return new TsvSection(
            "nei-handlers",
            Arrays.asList(
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
                "reason"),
            rows);
    }

    private static void addRecipeVisualFactsSections(List<TsvSection> sections, RecipeVisualFactsExportModel model) {
        SlotExtraction extraction = model.getExtraction();
        sections.add(extractedHandlerSection(extraction.getHandlers()));
        sections.add(extractedRecipeSection(extraction.getRecipes()));
        sections.add(extractedStackSection(extraction.getStacks()));
        sections.add(extractedCandidateSection(extraction.getCandidates()));
    }

    private static TsvSection extractedHandlerSection(List<ExtractedHandler> handlers) {
        List<List<String>> rows = new ArrayList<>();
        for (ExtractedHandler handler : handlers) {
            NeiHandlerDescriptor descriptor = handler.getDescriptor();
            rows.add(
                row(
                    descriptor.getStableKey(),
                    descriptor.getHandlerClass(),
                    descriptor.getResolvedCategoryId(),
                    descriptor.getSourceList(),
                    descriptor.getHandlerId(),
                    descriptor.getOverlayId(),
                    descriptor.getRecipeName(),
                    descriptor.getRecipeTabName(),
                    descriptor.getModId(),
                    descriptor.getModName(),
                    Integer.toString(handler.getRecipeCount()),
                    handler.getStatus(),
                    handler.getReason()));
        }
        return new TsvSection(
            "handlers",
            Arrays.asList(
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
                "reason"),
            rows);
    }

    private static TsvSection extractedRecipeSection(List<ExtractedRecipe> recipes) {
        List<List<String>> rows = new ArrayList<>();
        for (ExtractedRecipe recipe : recipes) {
            rows.add(
                row(
                    recipe.getHandlerKey(),
                    Integer.toString(recipe.getRecipeIndex()),
                    recipe.getFingerprint(),
                    Integer.toString(recipe.getIngredientCount()),
                    Integer.toString(recipe.getResultCount()),
                    Integer.toString(recipe.getOtherCount()),
                    recipe.getStatus(),
                    recipe.getReason()));
        }
        return new TsvSection(
            "recipes",
            Arrays.asList(
                "handler_key",
                "recipe_index",
                "recipe_fingerprint",
                "ingredient_count",
                "result_count",
                "other_count",
                "status",
                "reason"),
            rows);
    }

    private static TsvSection extractedStackSection(List<ExtractedStack> stacks) {
        List<List<String>> rows = new ArrayList<>();
        for (ExtractedStack stack : stacks) {
            rows.add(
                row(
                    stack.getHandlerKey(),
                    Integer.toString(stack.getRecipeIndex()),
                    stack.getStackSource(),
                    Integer.toString(stack.getStackIndex()),
                    Integer.toString(stack.getX()),
                    Integer.toString(stack.getY()),
                    Integer.toString(stack.getCandidateCount()),
                    stack.getFirstCandidate(),
                    stack.getStatus(),
                    stack.getReason()));
        }
        return new TsvSection(
            "stacks",
            Arrays.asList(
                "handler_key",
                "recipe_index",
                "stack_source",
                "stack_index",
                "x",
                "y",
                "candidate_count",
                "first_candidate",
                "status",
                "reason"),
            rows);
    }

    private static TsvSection extractedCandidateSection(List<ExtractedCandidate> candidates) {
        List<List<String>> rows = new ArrayList<>();
        for (ExtractedCandidate candidate : candidates) {
            rows.add(
                row(
                    candidate.getHandlerKey(),
                    Integer.toString(candidate.getRecipeIndex()),
                    candidate.getStackSource(),
                    Integer.toString(candidate.getStackIndex()),
                    Integer.toString(candidate.getCandidateIndex()),
                    candidate.getItemId(),
                    Integer.toString(candidate.getDamage()),
                    Integer.toString(candidate.getStackSize()),
                    candidate.getDisplayName()));
        }
        return new TsvSection(
            "candidates",
            Arrays.asList(
                "handler_key",
                "recipe_index",
                "stack_source",
                "stack_index",
                "candidate_index",
                "item_id",
                "damage",
                "stack_size",
                "display_name"),
            rows);
    }

    private static List<String> row(String... values) {
        return Arrays.asList(values);
    }

    private static final class TsvSection {

        final String name;
        final List<String> columns;
        final List<List<String>> rows;

        TsvSection(String name, List<String> columns, List<List<String>> rows) {
            this.name = name;
            this.columns = columns;
            this.rows = rows;
        }
    }
}
