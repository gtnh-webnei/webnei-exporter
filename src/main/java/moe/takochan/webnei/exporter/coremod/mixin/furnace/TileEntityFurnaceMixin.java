package moe.takochan.webnei.exporter.coremod.mixin.furnace;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import moe.takochan.webnei.exporter.domain.item.internal.TooltipBurnTimeOverride;

@Mixin(TileEntityFurnace.class)
public abstract class TileEntityFurnaceMixin {

    @Inject(method = "getItemBurnTime", at = @At("HEAD"), cancellable = true)
    private static void webnei$reuseTooltipBurnTime(ItemStack stack, CallbackInfoReturnable<Integer> callback) {
        Integer override = TooltipBurnTimeOverride.overrideFor(stack);
        if (override != null) {
            callback.setReturnValue(override);
        }
    }
}
