package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.Collections;

import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeCandidateMetadata;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCandidateProtocol;

/** Creates candidate observations for each approved presentation carrier. */
final class RecipeCandidateObservations {

    private RecipeCandidateObservations() {}

    static RecipeCandidateObservation itemStack(String itemVariantId, int amount, RecipeCandidateMetadata metadata) {
        return new RecipeCandidateObservation(
            RecipeCandidateProtocol.TARGET_DOMAIN_ITEM,
            itemVariantId,
            amount,
            RecipeCandidateProtocol.PRESENTATION_TYPE_ITEM_STACK,
            itemVariantId,
            RecipeCandidateProtocol.AMOUNT_UNIT_ITEM,
            metadata.getProbability(),
            metadata.getFragments());
    }

    static RecipeCandidateObservation fluidDisplay(String fluidId, int amount, String presentationType,
        String carrierItemVariantId, String amountUnit, RecipeCandidateMetadata metadata) {
        return new RecipeCandidateObservation(
            RecipeCandidateProtocol.TARGET_DOMAIN_FLUID,
            fluidId,
            amount,
            presentationType,
            carrierItemVariantId,
            amountUnit,
            metadata.getProbability(),
            metadata.getFragments());
    }

    static RecipeCandidateObservation fluidSlot(String fluidId, int amount) {
        return new RecipeCandidateObservation(
            RecipeCandidateProtocol.TARGET_DOMAIN_FLUID,
            fluidId,
            amount,
            RecipeCandidateProtocol.PRESENTATION_TYPE_FLUID_SLOT,
            fluidId,
            RecipeCandidateProtocol.AMOUNT_UNIT_MILLIBUCKET,
            RecipeCandidateProtocol.DEFAULT_PROBABILITY,
            Collections.emptyList());
    }
}
