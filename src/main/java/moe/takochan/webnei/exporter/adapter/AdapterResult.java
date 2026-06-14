package moe.takochan.webnei.exporter.adapter;

import codechicken.nei.recipe.IRecipeHandler;

public final class AdapterResult {

    public final String adapterId;
    public final AdapterStatus status;
    public final IRecipeHandler loadedHandler;
    public final AdapterLoadingSource loadingSource;
    public final String loadingKey;
    public final String reason;

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
        if (!loadingSource.label.isEmpty()) {
            appendSeparator(out);
            out.append("loading=")
                .append(loadingSource.label);
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
