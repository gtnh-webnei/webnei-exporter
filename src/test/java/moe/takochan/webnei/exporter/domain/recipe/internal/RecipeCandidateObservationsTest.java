package moe.takochan.webnei.exporter.domain.recipe.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeCandidateMetadata;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCandidateProtocol;

class RecipeCandidateObservationsTest {

    @Test
    void ordinaryItemUsesExactVariantPresentation() {
        RecipeCandidateObservation candidate = RecipeCandidateObservations
            .itemStack("mod:item@variant", 3, RecipeCandidateMetadata.defaults());

        assertEquals(RecipeCandidateProtocol.TARGET_DOMAIN_ITEM, candidate.getTargetDomain());
        assertEquals("mod:item@variant", candidate.getTargetId());
        assertEquals(RecipeCandidateProtocol.PRESENTATION_TYPE_ITEM_STACK, candidate.getPresentationType());
        assertEquals("mod:item@variant", candidate.getPresentationId());
        assertEquals(RecipeCandidateProtocol.AMOUNT_UNIT_ITEM, candidate.getAmountUnit());
    }

    @Test
    void gtDisplayKeepsFluidTargetAndExactCarrierVariant() {
        RecipeCandidateObservation candidate = RecipeCandidateObservations.fluidDisplay(
            "water",
            1000,
            RecipeCandidateProtocol.PRESENTATION_TYPE_GT_FLUID_DISPLAY,
            "gregtech:gt.display.fluid@variant",
            RecipeCandidateProtocol.AMOUNT_UNIT_LITER,
            RecipeCandidateMetadata.defaults());

        assertEquals(RecipeCandidateProtocol.TARGET_DOMAIN_FLUID, candidate.getTargetDomain());
        assertEquals("water", candidate.getTargetId());
        assertEquals(RecipeCandidateProtocol.PRESENTATION_TYPE_GT_FLUID_DISPLAY, candidate.getPresentationType());
        assertEquals("gregtech:gt.display.fluid@variant", candidate.getPresentationId());
        assertEquals(RecipeCandidateProtocol.AMOUNT_UNIT_LITER, candidate.getAmountUnit());
    }

    @Test
    void extraFluidSlotUsesFluidIdPresentationWithoutFragments() {
        RecipeCandidateObservation candidate = RecipeCandidateObservations.fluidSlot("molten.iron", 144);

        assertEquals(RecipeCandidateProtocol.TARGET_DOMAIN_FLUID, candidate.getTargetDomain());
        assertEquals(RecipeCandidateProtocol.PRESENTATION_TYPE_FLUID_SLOT, candidate.getPresentationType());
        assertEquals("molten.iron", candidate.getPresentationId());
        assertEquals(RecipeCandidateProtocol.AMOUNT_UNIT_MILLIBUCKET, candidate.getAmountUnit());
        assertEquals(1.0d, candidate.getProbability());
        assertTrue(
            candidate.getFragments()
                .isEmpty());
    }
}
