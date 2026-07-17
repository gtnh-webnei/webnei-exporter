package moe.takochan.webnei.exporter.bundle.record.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import moe.takochan.webnei.exporter.bundle.record.BundleRecordSet;
import moe.takochan.webnei.exporter.domain.recipe.RecipeExportModel;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCandidateTooltipFragmentRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeSlotCandidateRow;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeTooltipRegionRow;

class RecipeRecordSetMapperTest {

    @Test
    void candidateFragmentAndRegionFieldsFollowSchemaOrder() {
        RecipeSlotCandidateRow candidate = new RecipeSlotCandidateRow(
            "d",
            "r",
            "input@1,2",
            0,
            "item",
            "mod:item@variant",
            2,
            0.5d,
            "itemStack",
            "mod:item@variant",
            "item");
        RecipeCandidateTooltipFragmentRow fragment = new RecipeCandidateTooltipFragmentRow(
            "d",
            "r",
            "input@1,2",
            0,
            0,
            "all",
            "Chance: 50%");
        RecipeTooltipRegionRow region = new RecipeTooltipRegionRow(
            "d",
            "r",
            0,
            "fluidTank",
            3,
            4,
            16,
            20,
            "all",
            "Water\n§71000 mB");
        RecipeExportModel model = new RecipeExportModel(
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.singletonList(candidate),
            Collections.singletonList(fragment),
            Collections.singletonList(region));

        List<BundleRecordSet> sets = new RecipeRecordSetMapper().recordSets(model);
        BundleRecordSet candidates = sets.get(4);
        BundleRecordSet fragments = sets.get(5);
        BundleRecordSet regions = sets.get(6);

        assertEquals("recipe_slot_candidate", candidates.getName());
        assertEquals(140, candidates.getOrder());
        assertEquals(
            Arrays.asList(
                "dataset_id",
                "recipe_id",
                "slot_key",
                "candidate_order",
                "target_domain",
                "target_id",
                "amount",
                "probability",
                "presentation_type",
                "presentation_id",
                "amount_unit"),
            candidates.getFields());
        assertEquals("recipe_candidate_tooltip_fragment", fragments.getName());
        assertEquals(145, fragments.getOrder());
        assertEquals(
            Arrays.asList(
                "dataset_id",
                "recipe_id",
                "slot_key",
                "candidate_order",
                "fragment_order",
                "state_key",
                "text_value"),
            fragments.getFields());
        assertEquals(
            Arrays.asList("d", "r", "input@1,2", "0", "0", "all", "Chance: 50%"),
            fragments.getRecords()
                .get(0));
        assertEquals("recipe_tooltip_region", regions.getName());
        assertEquals(150, regions.getOrder());
        assertEquals(
            Arrays.asList(
                "dataset_id",
                "recipe_id",
                "region_order",
                "region_type",
                "x",
                "y",
                "width",
                "height",
                "state_key",
                "tooltip_text"),
            regions.getFields());
    }
}
