package moe.takochan.webnei.exporter.domain.asset.render;

public final class AssetRenderMetadata {

    public static final int TICK_MS = 50;

    private static final String STATIC_IMAGE_METADATA = "{}";
    private static final String ANIMATION_TYPE_SPRITESHEET = "spritesheet";
    private static final String ANIMATION_LAYOUT_HORIZONTAL = "horizontal";
    private static final String ANIMATION_ENCODING_EXPANDED_TICKS = "expanded_ticks";

    private AssetRenderMetadata() {}

    public static String staticImage() {
        return STATIC_IMAGE_METADATA;
    }

    public static String horizontalExpandedTicks(int frameWidth, int frameHeight, int frameCount) {
        return "{\"animation\":{" + "\"type\":\""
            + ANIMATION_TYPE_SPRITESHEET
            + "\","
            + "\"layout\":\""
            + ANIMATION_LAYOUT_HORIZONTAL
            + "\","
            + "\"encoding\":\""
            + ANIMATION_ENCODING_EXPANDED_TICKS
            + "\","
            + "\"frameWidth\":"
            + frameWidth
            + ','
            + "\"frameHeight\":"
            + frameHeight
            + ','
            + "\"frameCount\":"
            + frameCount
            + ','
            + "\"tickMs\":"
            + TICK_MS
            + "}}";
    }
}
