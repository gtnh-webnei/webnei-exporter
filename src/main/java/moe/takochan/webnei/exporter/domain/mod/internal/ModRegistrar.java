package moe.takochan.webnei.exporter.domain.mod.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cpw.mods.fml.common.InjectedModContainer;
import cpw.mods.fml.common.ModContainer;
import moe.takochan.webnei.exporter.WebneiExporterMod;
import moe.takochan.webnei.exporter.domain.mod.model.ModRow;
import moe.takochan.webnei.exporter.engine.store.IDomainRegistrar;

/**
 * mod 注册处理器 — 负责从 ModContainer 采集字段并写入 data。
 */
public final class ModRegistrar implements IDomainRegistrar {

    private static final String UNKNOWN = "";
    private static final String MOD_ID_MCP = "mcp";
    private static final String MINECRAFT_JAR_PLACEHOLDER = "minecraft.jar";
    private static final String SOURCE_TYPE_FILE = "file";
    private static final String SOURCE_TYPE_DIRECTORY = "directory";
    private static final String SOURCE_TYPE_UNKNOWN = "unknown";
    private static final int HASH_BUFFER_SIZE = 64 * 1024;

    private final ModDomainData data;
    private final String datasetId;

    public ModRegistrar(ModDomainData data, String datasetId) {
        this.data = data;
        this.datasetId = datasetId;
    }

    public void register(ModContainer mod) {
        File source = resolveSource(mod);
        ModRow row = new ModRow(
            datasetId,
            value(mod.getModId()),
            value(mod.getName()),
            value(mod.getVersion()),
            sourceType(source),
            sourceName(source),
            sourceSha256(mod, source),
            true);
        data.put(row);
    }

    private static String value(String value) {
        return value == null ? UNKNOWN : value;
    }

    private static File resolveSource(ModContainer mod) {
        File source = mod.getSource();
        if (isInjectedMinecraftJarPlaceholder(mod, source)) {
            File wrappedSource = classSource(((InjectedModContainer) mod).wrappedContainer.getClass());
            if (wrappedSource != null) return wrappedSource;
        }
        if (source == null) {
            File containerSource = classSource(mod.getClass());
            if (containerSource != null) return containerSource;
        }
        return source;
    }

    private static boolean isInjectedMinecraftJarPlaceholder(ModContainer mod, File source) {
        return mod instanceof InjectedModContainer && !MOD_ID_MCP.equals(mod.getModId())
            && source != null
            && MINECRAFT_JAR_PLACEHOLDER.equals(source.getPath());
    }

    private static File classSource(Class<?> type) {
        CodeSource codeSource = type.getProtectionDomain()
            .getCodeSource();
        if (codeSource == null) return null;

        URL location = codeSource.getLocation();
        if (location == null || !SOURCE_TYPE_FILE.equals(location.getProtocol())) return null;

        try {
            return new File(location.toURI());
        } catch (URISyntaxException e) {
            return new File(location.getPath());
        }
    }

    private static String sourceType(File source) {
        if (source == null) return SOURCE_TYPE_UNKNOWN;
        if (source.isFile()) return SOURCE_TYPE_FILE;
        if (source.isDirectory()) return SOURCE_TYPE_DIRECTORY;
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
