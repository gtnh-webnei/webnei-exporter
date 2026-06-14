package moe.takochan.webnei.exporter.nei.loading;

import codechicken.nei.recipe.IRecipeHandler;

public final class NeiLoadingResult {

    public final NeiLoadingStatus status;
    public final IRecipeHandler handler;
    public final NeiLoadingSource source;
    public final String provider;
    public final String key;
    public final String reason;

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
        if (!source.label.isEmpty()) {
            appendSeparator(out);
            out.append("source=")
                .append(source.label);
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
