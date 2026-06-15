package moe.takochan.webnei.exporter.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** 给参与稳定 ID 的长文本生成短 hash。 */
public final class StableHash {

    private static final int DEFAULT_HEX_LENGTH = 16;

    private StableHash() {}

    public static String shortHash(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        byte[] digest = digest(value);
        StringBuilder builder = new StringBuilder(digest.length * 2);
        for (byte b : digest) {
            builder.append(String.format("%02x", b & 0xff));
        }
        return builder.substring(0, Math.min(DEFAULT_HEX_LENGTH, builder.length()));
    }

    private static byte[] digest(String value) {
        try {
            return MessageDigest.getInstance("SHA-256")
                .digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
