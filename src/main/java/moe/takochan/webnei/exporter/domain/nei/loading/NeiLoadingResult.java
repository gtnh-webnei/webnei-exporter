package moe.takochan.webnei.exporter.domain.nei.loading;

import codechicken.nei.recipe.IRecipeHandler;
import lombok.Getter;

/** NEI handler 加载支持返回的结果。 */
@Getter
public final class NeiLoadingResult {

    /** 加载状态。 */
    private final NeiLoadingStatus status;

    /** 成功加载出的实际 recipe handler；非成功状态下为空。 */
    private final IRecipeHandler handler;

    /** 本次加载使用的来源。 */
    private final NeiLoadingSource source;

    /** 加载实现或 provider 名称。 */
    private final String provider;

    /** 加载来源使用的 key。 */
    private final String key;

    /** 状态补充说明或错误原因。 */
    private final String reason;

    private NeiLoadingResult(NeiLoadingStatus status, IRecipeHandler handler, NeiLoadingSource source, String provider,
        String key, String reason) {
        this.status = status;
        this.handler = handler;
        this.source = source;
        this.provider = provider;
        this.key = key;
        this.reason = reason;
    }

    public static NeiLoadingResult loaded(IRecipeHandler handler, NeiLoadingSource source, String provider,
        String key) {
        return new NeiLoadingResult(NeiLoadingStatus.LOADED, handler, source, provider, key, "");
    }

    public static NeiLoadingResult unsupported() {
        return new NeiLoadingResult(
            NeiLoadingStatus.UNSUPPORTED,
            null,
            NeiLoadingSource.NONE,
            "",
            "",
            "no loading support");
    }

    public static NeiLoadingResult error(NeiLoadingSource source, String provider, String key, String reason) {
        return new NeiLoadingResult(NeiLoadingStatus.ERROR, null, source, provider, key, reason);
    }

    public String describe() {
        StringBuilder out = new StringBuilder();
        if (!source.getLabel()
            .isEmpty()) {
            appendSeparator(out);
            out.append("source=")
                .append(source.getLabel());
        }
        if (!provider.isEmpty()) {
            appendSeparator(out);
            out.append("provider=")
                .append(provider);
        }
        if (!key.isEmpty()) {
            appendSeparator(out);
            out.append("key=")
                .append(key);
        }
        if (!reason.isEmpty()) {
            appendSeparator(out);
            out.append(reason);
        }
        return out.toString();
    }

    private static void appendSeparator(StringBuilder out) {
        if (out.length() > 0) {
            out.append(" | ");
        }
    }
}
