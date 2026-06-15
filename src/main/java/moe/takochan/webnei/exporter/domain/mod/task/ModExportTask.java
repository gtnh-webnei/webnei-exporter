package moe.takochan.webnei.exporter.domain.mod.task;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import moe.takochan.webnei.exporter.Tags;
import moe.takochan.webnei.exporter.WebneiExporterMod;
import moe.takochan.webnei.exporter.domain.mod.DatasetModExportModel;
import moe.takochan.webnei.exporter.domain.mod.model.DatasetRow;
import moe.takochan.webnei.exporter.domain.mod.model.ModRow;
import moe.takochan.webnei.exporter.engine.task.ExportTaskContext;
import moe.takochan.webnei.exporter.engine.task.IExportTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * dataset / mod 基础数据导出任务。
 *
 * <p>
 * dataset 是一次导出的核心关联索引。后续 item、fluid、recipe、asset 等数据都必须挂在同一个
 * dataset 上，并通过 mod id / mod name 关联到 mod 基础信息。
 */
public final class ModExportTask implements IExportTask {

    public static final String ID = "mod-export";
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
    public void execute(ExportTaskContext context) {
        context.addModel(new DatasetModExportModel(datasetRow(identity), modRows(identity)));
    }

    private static DatasetRow datasetRow(DatasetIdentity identity) {
        return new DatasetRow(
            identity.getDatasetId(),
            identity.getPackSlug(),
            identity.getPackVersion(),
            identity.getVariant(),
            identity.getLanguage(),
            identity.getDisplayName(),
            SCHEMA_VERSION,
            Tags.VERSION,
            exportedAt(),
            Loader.MC_VERSION);
    }

    private static String exportedAt() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(new Date());
    }

    private static List<ModRow> modRows(DatasetIdentity identity) {
        List<ModContainer> mods = new ArrayList<>(
            Loader.instance()
                .getActiveModList());
        mods.sort(Comparator.comparing((ModContainer left) -> value(left.getModId())).thenComparing(ModExportTask::sourceName));

        List<ModRow> rows = new ArrayList<>();
        for (ModContainer mod : mods) {
            File source = mod.getSource();
            rows.add(
                new ModRow(
                    identity.getDatasetId(),
                    value(mod.getModId()),
                    value(mod.getName()),
                    value(mod.getVersion()),
                    sourceType(source),
                    sourceName(source),
                    sourceSha256(mod, source),
                    true));
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
}
