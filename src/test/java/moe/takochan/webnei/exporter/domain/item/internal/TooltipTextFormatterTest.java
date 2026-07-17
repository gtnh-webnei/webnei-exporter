package moe.takochan.webnei.exporter.domain.item.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class TooltipTextFormatterTest {

    private static final String RARITY = "\u00a7d";

    @Test
    void wrapsRarityAndBodyLines() {
        assertEquals(
            "\u00a7dName\n\u00a77Body\u00a7r",
            TooltipTextFormatter.format(RARITY, Arrays.asList("Name", "Body")));
    }

    @Test
    void preservesEmptyAndCustomRendererLines() {
        assertEquals(
            "\u00a7dName\n\n\u00a7xcustom",
            TooltipTextFormatter.format(RARITY, Arrays.asList("Name", null, "\u00a7xcustom")));
    }

    @Test
    void usesUnnamedForEmptyTooltip() {
        assertEquals("\u00a7dUnnamed", TooltipTextFormatter.format(RARITY, Arrays.<String>asList()));
    }

    @Test
    void usesRarityUnnamedForTooltipExceptionFallback() {
        assertEquals("\u00a7dUnnamed", TooltipTextFormatter.formatFailure(RARITY));
    }

    @Test
    void usesUnnamedWhenRarityCollectionFails() {
        assertEquals("Unnamed", TooltipTextFormatter.formatFailure(""));
    }
}
