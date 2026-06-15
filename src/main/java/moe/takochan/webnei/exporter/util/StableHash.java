package moe.takochan.webnei.exporter.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** 给参与稳定 ID 的长文本生成短 hash。 */
public final class StableHash {

    /** 默认输出的十六进制字符数，足够短用于 ID，同时降低冲突概率。 */
    private static final int DEFAULT_HEX_LENGTH = 16;

    private StableHash() {}

    /**
     * 生成稳定短 hash。
     *
     * <p>
     * 空值保持为空，避免无 NBT 和空 NBT 被写成额外 hash 片段。
     */
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

    /** 对输入文本按 UTF-8 计算 SHA-256。 */
    private static byte[] digest(String value) {
        try {
            return MessageDigest.getInstance("SHA-256")
                .digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
