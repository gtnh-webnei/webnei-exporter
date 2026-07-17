package moe.takochan.webnei.exporter.hook.gregtech;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeCandidateMetadata;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeTooltipFragmentObservation;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeTooltipProtocol;

class GregTechRecipeCandidateMetadataHookTest {

    @Test
    void chanceBoundariesUseTenThousandDenominator() {
        assertEquals(0.0d, metadata(true, 0).getProbability());
        assertEquals(0.0001d, metadata(true, 1).getProbability());
        assertEquals(0.9999d, metadata(true, 9999).getProbability());
        assertEquals(1.0d, metadata(true, 10000).getProbability());
        assertEquals(1.0d, metadata(false, 1).getProbability());
    }

    @Test
    void eachFrontendAppendBecomesOneOrderedAllStateFragment() {
        RecipeCandidateMetadata metadata = GregTechRecipeCandidateMetadataHook
            .metadata(false, 0, Arrays.asList("first", "second"));

        assertEquals(
            2,
            metadata.getFragments()
                .size());
        RecipeTooltipFragmentObservation first = metadata.getFragments()
            .get(0);
        RecipeTooltipFragmentObservation second = metadata.getFragments()
            .get(1);
        assertEquals(RecipeTooltipProtocol.STATE_ALL, first.getStateKey());
        assertEquals("first", first.getTextValue());
        assertEquals(RecipeTooltipProtocol.STATE_ALL, second.getStateKey());
        assertEquals("second", second.getTextValue());
        assertTrue(
            metadata(false, 0).getFragments()
                .isEmpty());
    }

    @Test
    void implementationUsesNarrowFrontendApiWithoutGeneralHandlersOrTextParsing() throws IOException {
        String source = new String(
            Files.readAllBytes(
                Paths.get(
                    "src/main/java/moe/takochan/webnei/exporter/hook/gregtech/GregTechRecipeCandidateMetadataHook.java")),
            StandardCharsets.UTF_8);

        assertTrue(source.contains("frontend.handleNEIItemTooltip"));
        assertFalse(source.contains("handler.handleTooltip"));
        assertFalse(source.contains("handler.handleItemTooltip"));
        assertFalse(source.contains("drawNEIOverlays"));
        assertFalse(source.contains("Double.parseDouble"));
    }

    private static RecipeCandidateMetadata metadata(boolean chanceBased, int chance) {
        return GregTechRecipeCandidateMetadataHook.metadata(chanceBased, chance, Collections.<String>emptyList());
    }
}
