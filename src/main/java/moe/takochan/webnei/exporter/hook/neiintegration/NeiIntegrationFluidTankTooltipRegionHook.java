package moe.takochan.webnei.exporter.hook.neiintegration;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidStack;

import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeTooltipRegionHook;
import moe.takochan.webnei.exporter.domain.recipe.hook.RecipeTooltipRegionObservation;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeTooltipProtocol;
import tonius.neiintegration.PositionedFluidTank;
import tonius.neiintegration.RecipeHandlerBase;

/** Reproduces NEI-Integration fluid-tank tooltip regions from public cached recipe data. */
public final class NeiIntegrationFluidTankTooltipRegionHook implements IRecipeTooltipRegionHook {

    @Override
    public boolean isAvailable() {
        return Mods.NEI_INTEGRATION.isLoaded();
    }

    @Override
    public boolean supports(IRecipeHandler handler) {
        return handler instanceof RecipeHandlerBase;
    }

    @Override
    public List<RecipeTooltipRegionObservation> collect(IRecipeHandler handler, int recipeIndex) {
        RecipeHandlerBase recipeHandler = (RecipeHandlerBase) handler;
        TemplateRecipeHandler.CachedRecipe cached = recipeHandler.arecipes.get(recipeIndex);
        RecipeHandlerBase.CachedBaseRecipe recipe = (RecipeHandlerBase.CachedBaseRecipe) cached;
        List<PositionedFluidTank> tanks = recipe.getFluidTanks();
        if (tanks == null || tanks.isEmpty()) {
            return Collections.emptyList();
        }
        List<RecipeTooltipRegionObservation> out = new ArrayList<>(tanks.size());
        for (PositionedFluidTank tank : tanks) {
            RecipeTooltipRegionObservation region = region(tank);
            if (region != null) {
                out.add(region);
            }
        }
        return out;
    }

    private static RecipeTooltipRegionObservation region(PositionedFluidTank positioned) {
        if (positioned.tank == null) {
            return null;
        }
        FluidStack fluid = positioned.tank.getFluid();
        if (fluid == null || fluid.getFluid() == null || fluid.amount <= 0) {
            return null;
        }
        Rectangle position = positioned.position;
        List<String> lines = new ArrayList<>(2);
        lines.add(fluid.getLocalizedName());
        if (positioned.showAmount) {
            lines.add(EnumChatFormatting.GRAY.toString() + fluid.amount + (positioned.perTick ? " mB/t" : " mB"));
        }
        return new RecipeTooltipRegionObservation(
            RecipeTooltipProtocol.REGION_TYPE_FLUID_TANK,
            position.x,
            position.y,
            position.width,
            position.height,
            RecipeTooltipProtocol.STATE_ALL,
            joinLines(lines));
    }

    private static String joinLines(List<String> lines) {
        StringBuilder out = new StringBuilder();
        for (String line : lines) {
            if (out.length() > 0) {
                out.append('\n');
            }
            out.append(line);
        }
        return out.toString();
    }
}
