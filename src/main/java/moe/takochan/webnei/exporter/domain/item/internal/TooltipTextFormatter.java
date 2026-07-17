package moe.takochan.webnei.exporter.domain.item.internal;

import java.util.ArrayList;
import java.util.List;

/** 按 NEI 基础 tooltip 规则包装 normal tooltip 文本。 */
public final class TooltipTextFormatter {

    static final String UNNAMED = "Unnamed";
    static final String BODY_PREFIX = "\u00a77";
    static final String RESET = "\u00a7r";
    static final String CUSTOM_RENDERER_PREFIX = "\u00a7x";

    private TooltipTextFormatter() {}

    public static String format(String rarityColor, List<String> rawLines) {
        List<String> lines = rawLines == null ? new ArrayList<String>() : new ArrayList<>(rawLines);
        if (lines.isEmpty()) {
            lines.add(UNNAMED);
        } else if (lines.get(0) == null || lines.get(0)
            .isEmpty()) {
                lines.set(0, UNNAMED);
            }
        lines.set(0, rarityColor + lines.get(0));
        for (int i = 1; i < lines.size(); i++) {
            String raw = lines.get(i);
            if (raw == null) {
                lines.set(i, "");
            } else if (!raw.startsWith(CUSTOM_RENDERER_PREFIX)) {
                lines.set(i, BODY_PREFIX + raw + RESET);
            }
        }
        return join(lines);
    }

    public static String formatFailure(String rarityColor) {
        return rarityColor + UNNAMED;
    }

    private static String join(List<String> lines) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                text.append('\n');
            }
            text.append(lines.get(i));
        }
        return text.toString();
    }
}
