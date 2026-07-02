package moe.takochan.webnei.exporter.hook.gregtech;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.Loader;
import gregtech.api.enums.Mods;
import gregtech.common.items.ItemFluidDisplay;
import moe.takochan.webnei.exporter.domain.fluid.hook.IFluidDisplayStackHook;

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
}
