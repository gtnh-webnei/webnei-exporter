package moe.takochan.webnei.exporter.bundle.tsv;

/** TSV schema v4 单元格的可逆反斜杠编码。 */
public final class TsvCellCodec {

    private TsvCellCodec() {}

    public static String encode(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder encoded = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\\') {
                encoded.append("\\\\");
            } else if (c == '\t') {
                encoded.append("\\t");
            } else if (c == '\r') {
                encoded.append("\\r");
            } else if (c == '\n') {
                encoded.append("\\n");
            } else {
                encoded.append(c);
            }
        }
        return encoded.toString();
    }

    public static String decode(String value) {
        StringBuilder decoded = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c != '\\') {
                decoded.append(c);
                continue;
            }
            if (++i >= value.length()) {
                throw new IllegalArgumentException("Trailing TSV escape");
            }
            char escaped = value.charAt(i);
            if (escaped == '\\') {
                decoded.append('\\');
            } else if (escaped == 't') {
                decoded.append('\t');
            } else if (escaped == 'r') {
                decoded.append('\r');
            } else if (escaped == 'n') {
                decoded.append('\n');
            } else {
                throw new IllegalArgumentException("Unknown TSV escape: \\" + escaped);
            }
        }
        return decoded.toString();
    }
}
