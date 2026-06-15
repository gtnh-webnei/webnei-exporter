package moe.takochan.webnei.exporter.adapter;

import codechicken.nei.recipe.IRecipeHandler;
import lombok.Getter;

/** mod adapter 尝试处理 NEI handler 后返回的结果。 */
@Getter
public final class AdapterResult {

    /** 返回该结果的 adapter ID；无 adapter 命中时为空。 */
    private final String adapterId;

    /** adapter 处理状态。 */
    private final AdapterStatus status;

    /** adapter 成功加载出的实际 recipe handler；非成功状态下为空。 */
    private final IRecipeHandler loadedHandler;

    /** adapter 使用的加载来源。 */
    private final AdapterLoadingSource loadingSource;

    /** 加载来源使用的 key。 */
    private final String loadingKey;

    /** 状态补充说明或错误原因。 */
    private final String reason;

    private AdapterResult(String adapterId, AdapterStatus status, IRecipeHandler loadedHandler,
        AdapterLoadingSource loadingSource, String loadingKey, String reason) {
        this.adapterId = adapterId;
        this.status = status;
        this.loadedHandler = loadedHandler;
        this.loadingSource = loadingSource;
        this.loadingKey = loadingKey;
        this.reason = reason;
    }

    public static AdapterResult extracted(String adapterId, IRecipeHandler loadedHandler,
        AdapterLoadingSource loadingSource, String loadingKey) {
        return new AdapterResult(adapterId, AdapterStatus.EXTRACTED, loadedHandler, loadingSource, loadingKey, "");
    }

    public static AdapterResult skipped(String adapterId, String reason) {
        return new AdapterResult(adapterId, AdapterStatus.SKIPPED, null, AdapterLoadingSource.NONE, "", reason);
    }

    public static AdapterResult unsupported() {
        return new AdapterResult(
            "",
            AdapterStatus.UNSUPPORTED,
            null,
            AdapterLoadingSource.NONE,
            "",
            "no adapter support");
    }

    public static AdapterResult error(String adapterId, AdapterLoadingSource loadingSource, String loadingKey,
        String reason) {
        return new AdapterResult(adapterId, AdapterStatus.ERROR, null, loadingSource, loadingKey, reason);
    }

    public String describe() {
        StringBuilder out = new StringBuilder();
        if (!adapterId.isEmpty()) {
            out.append("adapter=")
                .append(adapterId);
        }
        if (!loadingSource.getLabel()
            .isEmpty()) {
            appendSeparator(out);
            out.append("loading=")
                .append(loadingSource.getLabel());
        }
        if (!loadingKey.isEmpty()) {
            appendSeparator(out);
            out.append("key=")
                .append(loadingKey);
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
