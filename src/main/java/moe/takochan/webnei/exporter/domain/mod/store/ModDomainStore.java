package moe.takochan.webnei.exporter.domain.mod.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import moe.takochan.webnei.exporter.WebneiExporterMod;
import moe.takochan.webnei.exporter.domain.IExportModel;
import moe.takochan.webnei.exporter.domain.mod.ModExportModel;
import moe.takochan.webnei.exporter.domain.mod.model.ModRow;
import moe.takochan.webnei.exporter.engine.store.IDomainStore;

import cpw.mods.fml.common.ModContainer;

/**
 * mod domain store。
 *
 * <p>
 * 输入为 ModContainer（Forge 运行时 mod 实例），内部处理来源类型判断、文件哈希等逻辑，
 * 输出为 ModRow。
 */
public final class ModDomainStore implements IDomainStore<ModContainer, ModRow> {

    private static final String UNKNOWN = "";
    private static final String SOURCE_TYPE_UNKNOWN = "unknown";
    private static final int HASH_BUFFER_SIZE = 64 * 1024;

    private final String datasetId;
    private final List<ModRow> rows = new ArrayList<>();

    public ModDomainStore(String datasetId) {
        this.datasetId = datasetId;
    }

    @Override
    public ModRow add(ModContainer mod) {
        ModRow row = new ModRow(
            datasetId,
            value(mod.getModId()),
            value(mod.getName()),
            value(mod.getVersion()),
            sourceType(mod.getSource()),
            sourceName(mod.getSource()),
            sourceSha256(mod, mod.getSource()),
            true);
        rows.add(row);
        return row;
    }

    @Override
    public ModRow get(String key) {
        for (ModRow row : rows) {
            if (row.getModId().equals(key)) {
                return row;
            }
        }
        return null;
    }

    @Override
    public List<ModRow> list() {
        return Collections.unmodifiableList(rows);
    }

    @Override
    public IExportModel toExportModel() {
        return new ModExportModel(rows);
    }

    private static String sourceType(File source) {
        if (source == null) return SOURCE_TYPE_UNKNOWN;
        if (source.isFile()) return "file";
        if (source.isDirectory()) return "directory";
        return SOURCE_TYPE_UNKNOWN;
    }

    private static String sourceName(File source) {
        return source == null ? UNKNOWN : value(source.getName());
    }

    private static String sourceSha256(ModContainer mod, File source) {
        if (source == null || !source.isFile()) return UNKNOWN;
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
        for (byte b : bytes) {
            builder.append(String.format("%02x", b & 0xff));
        }
        return builder.toString();
    }

    private static String value(String value) {
        return value == null ? UNKNOWN : value;
    }
}
