package moe.takochan.webnei.exporter.bundle;

import java.util.Locale;

/** bundle writer 支持的输出格式。 */
public enum BundleFormat {

    TSV,
    PGSQL;

    /** 未显式传入格式时使用的默认输出格式。 */
    private static final BundleFormat DEFAULT = TSV;

    /** 返回默认输出格式。 */
    public static BundleFormat defaultFormat() {
        return DEFAULT;
    }

    /** 判断命令参数是否是已支持的输出格式。 */
    public static boolean isFormat(String value) {
        return find(value) != null;
    }

    /** 从命令参数解析输出格式。 */
    public static BundleFormat parse(String value) {
        BundleFormat format = find(value);
        if (format == null) {
            throw new IllegalArgumentException("Unsupported bundle format: " + value);
        }
        return format;
    }

    /** 返回命令行中使用的格式名称。 */
    public String argumentName() {
        return name().toLowerCase(Locale.ROOT);
    }

    private static BundleFormat find(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        for (BundleFormat format : values()) {
            if (format.argumentName()
                .equalsIgnoreCase(normalized)) {
                return format;
            }
        }
        return null;
    }
}
