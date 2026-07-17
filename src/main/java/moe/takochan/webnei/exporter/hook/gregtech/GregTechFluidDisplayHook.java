package moe.takochan.webnei.exporter.hook.gregtech;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.Loader;
import gregtech.api.enums.Mods;
import gregtech.common.items.ItemFluidDisplay;
import moe.takochan.webnei.exporter.domain.fluid.hook.IFluidDisplayStackHook;
import moe.takochan.webnei.exporter.domain.recipe.model.RecipeCandidateProtocol;

/** 把 GregTech 的 {@link ItemFluidDisplay} 识别为流体显示占位 ItemStack。 */
public final class GregTechFluidDisplayHook implements IFluidDisplayStackHook {

    @Override
    public boolean isAvailable() {
        return Loader.isModLoaded(Mods.GregTech.ID);
    }

    @Override
    public boolean isFluidDisplay(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemFluidDisplay;
    }

    @Override
    public String presentationType() {
        return RecipeCandidateProtocol.PRESENTATION_TYPE_GT_FLUID_DISPLAY;
    }

    @Override
    public String amountUnit() {
        return RecipeCandidateProtocol.AMOUNT_UNIT_LITER;
    }
}
