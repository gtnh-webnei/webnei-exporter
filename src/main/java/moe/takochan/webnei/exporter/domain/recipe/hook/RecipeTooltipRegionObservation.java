package moe.takochan.webnei.exporter.domain.recipe.hook;

import lombok.Getter;

/** A non-slot tooltip region observed on one recipe page. */
@Getter
public final class RecipeTooltipRegionObservation {

    private final String regionType;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final String stateKey;
    private final String tooltipText;

    public RecipeTooltipRegionObservation(String regionType, int x, int y, int width, int height, String stateKey,
        String tooltipText) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Recipe tooltip region must have a positive rectangle");
        }
        this.regionType = regionType;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.stateKey = stateKey;
        this.tooltipText = tooltipText;
    }

    public RecipeTooltipRegionObservation withYShift(int yShift) {
        return new RecipeTooltipRegionObservation(regionType, x, y + yShift, width, height, stateKey, tooltipText);
    }
}
