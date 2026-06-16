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
import moe.takochan.webnei.exporter.domain.item.ItemExportModel;
import moe.takochan.webnei.exporter.domain.item.model.ItemListEntryRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemToolClassRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;
import moe.takochan.webnei.exporter.domain.mod.ModExportModel;
import moe.takochan.webnei.exporter.domain.mod.model.ModRow;

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
            } else if (model instanceof ModExportModel mod) {
                sections.add(modSection(mod.getMods()));
            } else if (model instanceof ItemExportModel item) {
                addItemSections(sections, item);
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
        sections.add(itemListEntrySection(model.getListEntries()));
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
                    variant.getChemicalExpression()));
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
                "chemical_expression"),
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

    private static TsvSection itemListEntrySection(List<ItemListEntryRow> entries) {
        List<List<String>> rows = new ArrayList<>();
        for (ItemListEntryRow entry : entries) {
            rows.add(row(entry.getDatasetId(), entry.getItemVariantId(), Integer.toString(entry.getListIndex())));
        }
        return new TsvSection("item_list_entry", Arrays.asList("dataset_id", "item_variant_id", "list_index"), rows);
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
