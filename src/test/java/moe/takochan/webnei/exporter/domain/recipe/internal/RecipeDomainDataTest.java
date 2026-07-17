package moe.takochan.webnei.exporter.domain.recipe.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import moe.takochan.webnei.exporter.domain.recipe.RecipeExportModel;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeCandidateMetadata;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeTooltipFragmentObservation;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeTooltipRegionObservation;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCandidateTooltipFragmentRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeSlotCandidateRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeTooltipProtocol;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeTooltipRegionRow;

class RecipeDomainDataTest {

    @Test
    void tooltipMetadataDoesNotChangeIdentityAndBindsIndependentlyToEachCandidate() {
        RecipeCandidateMetadata firstMetadata = new RecipeCandidateMetadata(
            0.5d,
            Arrays.asList(fragment("first-a"), fragment("first-b")));
        RecipeCandidateMetadata secondMetadata = new RecipeCandidateMetadata(
            0.25d,
            Collections.singletonList(fragment("second")));
        RecipeExportModel baseline = export(
            observation(RecipeCandidateMetadata.defaults(), RecipeCandidateMetadata.defaults()));
        RecipeExportModel withTooltips = export(observation(firstMetadata, secondMetadata));

        assertEquals(
            baseline.getRecipes()
                .get(0)
                .getRecipeId(),
            withTooltips.getRecipes()
                .get(0)
                .getRecipeId());
        assertEquals(
            baseline.getSlotLayouts()
                .get(0)
                .getSlotKey(),
            withTooltips.getSlotLayouts()
                .get(0)
                .getSlotKey());
        assertEquals(
            2,
            withTooltips.getSlotCandidates()
                .size());
        assertEquals(
            0.5d,
            withTooltips.getSlotCandidates()
                .get(0)
                .getProbability());
        assertEquals(
            0.25d,
            withTooltips.getSlotCandidates()
                .get(1)
                .getProbability());

        assertEquals(
            3,
            withTooltips.getCandidateTooltipFragments()
                .size());
        assertFragment(withTooltips, 0, 0, "first-a");
        assertFragment(withTooltips, 1, 0, "first-b");
        assertFragment(withTooltips, 2, 1, "second");
    }

    @Test
    void regionsKeepRecipeIdentityAndPreserveRectangleAndOrder() {
        RecipeVisualObservation baseline = observation(
            RecipeCandidateMetadata.defaults(),
            RecipeCandidateMetadata.defaults());
        RecipeVisualObservation withRegions = RecipeVisualObservation.of(
            baseline.getInputs(),
            baseline.getResult(),
            baseline.getOthers(),
            baseline.getExtraInputs(),
            baseline.getExtraOutputs(),
            Arrays.asList(
                new RecipeTooltipRegionObservation("fluidTank", 2, 3, 16, 20, "all", "Water"),
                new RecipeTooltipRegionObservation("fluidTank", 22, 3, 16, 20, "all", "Steam")));

        RecipeExportModel baselineExport = export(baseline);
        RecipeExportModel regionExport = export(withRegions);

        assertEquals(
            baselineExport.getRecipes()
                .get(0)
                .getRecipeId(),
            regionExport.getRecipes()
                .get(0)
                .getRecipeId());
        assertEquals(
            2,
            regionExport.getTooltipRegions()
                .size());
        RecipeTooltipRegionRow second = regionExport.getTooltipRegions()
            .get(1);
        assertEquals(1, second.getRegionOrder());
        assertEquals(22, second.getX());
        assertEquals(3, second.getY());
        assertEquals(16, second.getWidth());
        assertEquals(20, second.getHeight());
        assertEquals("Steam", second.getTooltipText());
    }

    @Test
    void duplicateVisualCoordinatesUseOccurrenceSuffixAcrossStandardAndExtraSlots() {
        RecipeSlotObservation standard = slot("mod:first@0", 1);
        RecipeSlotObservation extra = slot("mod:second@0", 2);
        RecipeVisualObservation observation = RecipeVisualObservation.of(
            Collections.singletonList(standard),
            null,
            Collections.emptyList(),
            Collections.singletonList(extra),
            Collections.emptyList(),
            Collections.emptyList());

        RecipeExportModel model = export(observation);

        assertEquals(
            2,
            model.getSlotLayouts()
                .size());
        assertEquals(
            "input@4,7",
            model.getSlotLayouts()
                .get(0)
                .getSlotKey());
        assertEquals(
            "input@4,7.2",
            model.getSlotLayouts()
                .get(1)
                .getSlotKey());
        assertEquals(
            "input@4,7",
            model.getSlotCandidates()
                .get(0)
                .getSlotKey());
        assertEquals(
            "input@4,7.2",
            model.getSlotCandidates()
                .get(1)
                .getSlotKey());
        assertEquals(
            0,
            model.getSlotCandidates()
                .get(0)
                .getCandidateOrder());
        assertEquals(
            0,
            model.getSlotCandidates()
                .get(1)
                .getCandidateOrder());
    }

    private static void assertFragment(RecipeExportModel model, int rowIndex, int candidateOrder, String text) {
        RecipeCandidateTooltipFragmentRow fragment = model.getCandidateTooltipFragments()
            .get(rowIndex);
        RecipeSlotCandidateRow parent = model.getSlotCandidates()
            .get(candidateOrder);
        assertEquals(parent.getRecipeId(), fragment.getRecipeId());
        assertEquals(parent.getSlotKey(), fragment.getSlotKey());
        assertEquals(parent.getCandidateOrder(), fragment.getCandidateOrder());
        assertEquals(RecipeTooltipProtocol.STATE_ALL, fragment.getStateKey());
        assertEquals(text, fragment.getTextValue());
        int expectedFragmentOrder = rowIndex == 1 ? 1 : 0;
        assertEquals(expectedFragmentOrder, fragment.getFragmentOrder());
    }

    private static RecipeTooltipFragmentObservation fragment(String text) {
        return new RecipeTooltipFragmentObservation(RecipeTooltipProtocol.STATE_ALL, text);
    }

    private static RecipeVisualObservation observation(RecipeCandidateMetadata first, RecipeCandidateMetadata second) {
        RecipeSlotObservation input = RecipeSlotObservation.of(
            4,
            7,
            Arrays.asList(
                RecipeCandidateObservations.itemStack("mod:first@0", 1, first),
                RecipeCandidateObservations.itemStack("mod:second@0", 2, second)));
        return RecipeVisualObservation.of(
            Collections.singletonList(input),
            null,
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList());
    }

    private static RecipeSlotObservation slot(String variantId, int amount) {
        return RecipeSlotObservation.of(
            4,
            7,
            Collections.singletonList(
                RecipeCandidateObservations.itemStack(variantId, amount, RecipeCandidateMetadata.defaults())));
    }

    private static RecipeExportModel export(RecipeVisualObservation observation) {
        RecipeCategoryIdentity identity = new RecipeCategoryIdentity(
            "handler",
            "mod:category",
            "mod:recipe",
            "Category",
            "mod",
            160,
            90,
            0,
            null,
            null);
        RecipeDomainData data = new RecipeDomainData("dataset");
        data.putIdentity(identity);
        data.registerVisual(identity, observation);
        return (RecipeExportModel) data.toExportModel();
    }
}
