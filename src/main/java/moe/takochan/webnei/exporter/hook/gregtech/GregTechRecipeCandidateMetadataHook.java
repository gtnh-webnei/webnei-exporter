package moe.takochan.webnei.exporter.hook.gregtech;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bartworks.API.recipe.BacterialVatFrontend;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import cpw.mods.fml.common.Loader;
import gregtech.api.enums.Mods;
import gregtech.api.recipe.RecipeMapFrontend;
import gregtech.api.recipe.maps.SpaceProjectFrontend;
import gregtech.nei.GTNEIDefaultHandler;
import gregtech.nei.GTNEIDefaultHandler.CachedDefaultRecipe;
import gregtech.nei.GTNEIDefaultHandler.FixedPositionedStack;
import gtPlusPlus.api.recipe.ChemicalPlantFrontend;
import gtPlusPlus.api.recipe.MillingFrontend;
import gtPlusPlus.api.recipe.SpargeTowerFrontend;
import gtnhlanth.common.tileentity.recipe.beamline.TargetChamberFrontend;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeCandidateMetadataHook;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeCandidateMetadata;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeTooltipFragmentObservation;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCandidateProtocol;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeTooltipProtocol;

/** Collects GT candidate probability and source-backed frontend tooltip appends. */
public final class GregTechRecipeCandidateMetadataHook implements IRecipeCandidateMetadataHook {

    private static final double CHANCE_DENOMINATOR = 10_000.0d;

    @Override
    public boolean isAvailable() {
        return Loader.isModLoaded(Mods.GregTech.ID);
    }

    @Override
    public boolean supports(IRecipeHandler handler) {
        return handler instanceof GTNEIDefaultHandler;
    }

    @Override
    public RecipeCandidateMetadata collect(IRecipeHandler handler, int recipeIndex, PositionedStack stack) {
        if (!(stack instanceof FixedPositionedStack)) {
            return RecipeCandidateMetadata.defaults();
        }
        FixedPositionedStack fixed = (FixedPositionedStack) stack;
        double probability = fixed.isChanceBased() ? fixed.mChance / CHANCE_DENOMINATOR
            : RecipeCandidateProtocol.DEFAULT_PROBABILITY;
        GTNEIDefaultHandler gt = (GTNEIDefaultHandler) handler;
        RecipeMapFrontend frontend = gt.getRecipeMap()
            .getFrontend();
        if (!isSupportedFrontend(frontend)) {
            return new RecipeCandidateMetadata(probability, Collections.<RecipeTooltipFragmentObservation>emptyList());
        }
        CachedDefaultRecipe recipe = (CachedDefaultRecipe) gt.arecipes.get(recipeIndex);
        List<String> lines = frontend.handleNEIItemTooltip(stack.item, new ArrayList<String>(), recipe);
        return new RecipeCandidateMetadata(probability, fragments(lines));
    }

    private static boolean isSupportedFrontend(RecipeMapFrontend frontend) {
        Class<?> type = frontend.getClass();
        return type == RecipeMapFrontend.class || type == BacterialVatFrontend.class
            || type == TargetChamberFrontend.class
            || type == ChemicalPlantFrontend.class
            || type == MillingFrontend.class
            || type == SpaceProjectFrontend.class
            || type == SpargeTowerFrontend.class;
    }

    static RecipeCandidateMetadata metadata(boolean chanceBased, int chance, List<String> lines) {
        double probability = chanceBased ? chance / CHANCE_DENOMINATOR : RecipeCandidateProtocol.DEFAULT_PROBABILITY;
        return new RecipeCandidateMetadata(probability, fragments(lines));
    }

    private static List<RecipeTooltipFragmentObservation> fragments(List<String> lines) {
        List<RecipeTooltipFragmentObservation> fragments = new ArrayList<>(lines.size());
        for (String line : lines) {
            fragments.add(new RecipeTooltipFragmentObservation(RecipeTooltipProtocol.STATE_ALL, line));
        }
        return fragments;
    }
}
