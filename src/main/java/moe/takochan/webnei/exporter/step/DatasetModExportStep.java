package moe.takochan.webnei.exporter.step;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import moe.takochan.webnei.exporter.Tags;
import moe.takochan.webnei.exporter.WebneiExporterMod;
import moe.takochan.webnei.exporter.export.ExportRequestOptions;
import moe.takochan.webnei.exporter.model.ExportRow;
import moe.takochan.webnei.exporter.model.ExportSection;

/**
 * dataset / mod 基础数据导出步骤。
 *
 * <p>
 * dataset 是一次导出的核心关联索引。后续 item、fluid、recipe、asset 等数据都必须挂在同一个
 * dataset 上，并通过 mod id / mod name 关联到 mod 基础信息。
 */
public final class DatasetModExportStep implements IExportStep {

    public static final String ID = "dataset-mod-export";
    public static final String SCHEMA_VERSION = "2";

    private static final String UNKNOWN = "";
    private static final String SOURCE_TYPE_UNKNOWN = "unknown";
    private static final int HASH_BUFFER_SIZE = 64 * 1024;

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String labelKey() {
        return "webnei.task.dataset";
    }

    @Override
    public void execute(ExportStepContext context) {
        DatasetIdentity identity = DatasetIdentity.from(context);
        context.setDatasetName(identity.datasetId);
        context.addSection(new ExportSection("dataset", datasetColumns(), datasetRows(identity)));
        context.addSection(new ExportSection("mods", modColumns(), modRows(identity)));
    }

    private static List<ExportRow> datasetRows(DatasetIdentity identity) {
        return Arrays.asList(
            ExportRow.of(
                identity.datasetId,
                identity.packSlug,
                identity.packVersion,
                identity.variant,
                identity.language,
                identity.displayName,
                SCHEMA_VERSION,
                Tags.VERSION,
                exportedAt(),
                Loader.MC_VERSION));
    }

    private static String exportedAt() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(new Date());
    }

    private static List<String> datasetColumns() {
        return Arrays.asList(
            "dataset_id",
            "pack_slug",
            "pack_version",
            "variant",
            "language",
            "display_name",
            "schema_version",
            "exporter_version",
            "created_at",
            "minecraft_version");
    }

    private static List<ExportRow> modRows(DatasetIdentity identity) {
        List<ModContainer> mods = new ArrayList<>(
            Loader.instance()
                .getActiveModList());
        Collections.sort(mods, new Comparator<ModContainer>() {

            @Override
            public int compare(ModContainer left, ModContainer right) {
                int modCompare = value(left.getModId()).compareTo(value(right.getModId()));
                if (modCompare != 0) {
                    return modCompare;
                }
                return sourceName(left).compareTo(sourceName(right));
            }
        });

        List<ExportRow> rows = new ArrayList<>();
        for (ModContainer mod : mods) {
            File source = mod.getSource();
            rows.add(
                ExportRow.of(
                    identity.datasetId,
                    value(mod.getModId()),
                    value(mod.getName()),
                    value(mod.getVersion()),
                    sourceType(source),
                    sourceName(source),
                    sourceSha256(mod, source),
                    Boolean.toString(true)));
        }
        return rows;
    }

    private static String sourceType(File source) {
        if (source == null) {
            return SOURCE_TYPE_UNKNOWN;
        }
        if (source.isFile()) {
            return "file";
        }
        if (source.isDirectory()) {
            return "directory";
        }
        return SOURCE_TYPE_UNKNOWN;
    }

    private static String sourceName(ModContainer mod) {
        return sourceName(mod.getSource());
    }

    private static String sourceName(File source) {
        return source == null ? UNKNOWN : value(source.getName());
    }

    private static String sourceSha256(ModContainer mod, File source) {
        if (source == null || !source.isFile()) {
            return UNKNOWN;
        }
        try {
            return sha256(source);
        } catch (IOException e) {
            WebneiExporterMod.LOG.warn("Could not hash mod source file {} for {}.", source, mod.getModId(), e);
            return UNKNOWN;
        }
    }

    private static String sha256(File file) throws IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            MessageDigest digest = newDigest();
            byte[] buffer = new byte[HASH_BUFFER_SIZE];
            int read;
            while ((read = inputStream.read(buffer)) >= 0) {
                digest.update(buffer, 0, read);
            }
            return toHex(digest.digest());
        }
    }

    private static MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            builder.append(String.format("%02x", value & 0xff));
        }
        return builder.toString();
    }

    private static String value(String value) {
        return value == null ? UNKNOWN : value;
    }

    private static List<String> modColumns() {
        return Arrays.asList(
            "dataset_id",
            "mod_id",
            "name",
            "version",
            "source_type",
            "source_file_name",
            "source_sha256",
            "enabled");
    }

    private static final class DatasetIdentity {

        final String packSlug;
        final String packVersion;
        final String variant;
        final String language;
        final String datasetId;
        final String displayName;

        private DatasetIdentity(String packSlug, String packVersion, String variant, String language) {
            this.packSlug = packSlug;
            this.packVersion = packVersion;
            this.variant = variant;
            this.language = language;
            this.datasetId = packSlug + ":" + packVersion + ":" + variant + ":" + language;
            this.displayName = packSlug + " " + packVersion + " " + variant + " (" + language + ")";
        }

        static DatasetIdentity from(ExportStepContext context) {
            return new DatasetIdentity(
                context.executionContext()
                    .request()
                    .option(ExportRequestOptions.PACK_SLUG),
                context.executionContext()
                    .request()
                    .option(ExportRequestOptions.PACK_VERSION),
                context.executionContext()
                    .request()
                    .option(ExportRequestOptions.VARIANT),
                currentLanguage());
        }

        private static String currentLanguage() {
            try {
                String language = FMLCommonHandler.instance()
                    .getCurrentLanguage();
                if (language != null && !language.trim()
                    .isEmpty()) {
                    return language.trim();
                }
            } catch (Throwable ignored) {}
            return "en_US";
        }
    }
}
