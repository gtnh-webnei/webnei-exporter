package moe.takochan.webnei.exporter.domain.mod.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cpw.mods.fml.common.ModContainer;
import moe.takochan.webnei.exporter.WebneiExporterMod;
import moe.takochan.webnei.exporter.domain.mod.model.ModRow;

/**
 * mod 注册处理器 — 负责从 ModContainer 采集字段并写入 data。
 */
public final class ModRegistrar {

    private static final String UNKNOWN = "";
    private static final String SOURCE_TYPE_UNKNOWN = "unknown";
    private static final int HASH_BUFFER_SIZE = 64 * 1024;

    private final ModDomainData data;
    private final String datasetId;

    public ModRegistrar(ModDomainData data, String datasetId) {
        this.data = data;
        this.datasetId = datasetId;
    }

    public void register(ModContainer mod) {
        ModRow row = new ModRow(
            datasetId,
            value(mod.getModId()),
            value(mod.getName()),
            value(mod.getVersion()),
            sourceType(mod.getSource()),
            sourceName(mod.getSource()),
            sourceSha256(mod, mod.getSource()),
            true);
        data.register(row);
    }

    private static String value(String value) {
        return value == null ? UNKNOWN : value;
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
}
