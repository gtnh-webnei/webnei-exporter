package moe.takochan.webnei.exporter.hook.tconstruct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import moe.takochan.webnei.exporter.compat.Mods;
import moe.takochan.webnei.exporter.domain.recipe.hook.ExtraRecipeSlot;
import moe.takochan.webnei.exporter.domain.recipe.hook.IRecipeSlotSourceHook;
import tconstruct.plugins.nei.RecipeHandlerAlloying;
import tconstruct.plugins.nei.RecipeHandlerBase;
import tconstruct.plugins.nei.RecipeHandlerBase.FluidTankElement;
import tconstruct.plugins.nei.RecipeHandlerCastingBasin;
import tconstruct.plugins.nei.RecipeHandlerCastingTable;
import tconstruct.plugins.nei.RecipeHandlerMelting;

/**
 * 从 Tinkers' Construct 熔炼体系 handler 的 {@code getFluidTanks()} 流体罐补充配方格子。
 *
 * <p>
 * 这些 handler 把流体输入 / 输出放在 {@code CachedBaseRecipe.getFluidTanks()}（{@link FluidTankElement}）里，不走 NEI
 * 标准 {@code PositionedStack} 接口，通用抽取看不到：alloying 整条配方全在流体罐里（原本完全为空）；melting 缺输出流体；
 * casting 缺输入流体。
 *
 * <p>
 * role 按各 handler 源码确定：
 * <ul>
 * <li>melting：流体罐是熔化输出。</li>
 * <li>casting（table / basin）：流体罐是浇铸原料输入。</li>
 * <li>alloying：第一个流体罐是合金产物输出，其余是熔融原料输入。</li>
 * </ul>
 */
public final class TConstructFluidTankSlotHook implements IRecipeSlotSourceHook {

    @Override
    public boolean isAvailable() {
        return Mods.TCONSTRUCT.isLoaded();
    }

    @Override
    public boolean supports(IRecipeHandler handler) {
        return handler instanceof RecipeHandlerMelting || handler instanceof RecipeHandlerCastingTable
            || handler instanceof RecipeHandlerCastingBasin
            || handler instanceof RecipeHandlerAlloying;
    }

    @Override
    public List<ExtraRecipeSlot> extractSlots(IRecipeHandler handler, int recipeIndex) {
        List<FluidTankElement> tanks = fluidTanks(handler, recipeIndex);
        if (tanks.isEmpty()) {
            return Collections.emptyList();
        }
        List<ExtraRecipeSlot> out = new ArrayList<>(tanks.size());
        for (int i = 0; i < tanks.size(); i++) {
            FluidTankElement tank = tanks.get(i);
            if (tank == null || tank.fluid == null || tank.position == null) {
                continue;
            }
            out.add(
                new ExtraRecipeSlot(
                    roleFor(handler, i),
                    tank.position.x,
                    tank.position.y,
                    Collections.singletonList(tank.fluid)));
        }
        return out;
    }

    /** melting 输出、casting 输入；alloying 首罐输出、其余输入。 */
    private static ExtraRecipeSlot.Role roleFor(IRecipeHandler handler, int tankIndex) {
        if (handler instanceof RecipeHandlerMelting) {
            return ExtraRecipeSlot.Role.OUTPUT;
        }
        if (handler instanceof RecipeHandlerAlloying) {
            return tankIndex == 0 ? ExtraRecipeSlot.Role.OUTPUT : ExtraRecipeSlot.Role.INPUT;
        }
        return ExtraRecipeSlot.Role.INPUT;
    }

    /** 从 handler 的 arecipes 取第 recipeIndex 个 cache 对象的流体罐列表。 */
    private static List<FluidTankElement> fluidTanks(IRecipeHandler handler, int recipeIndex) {
        if (!(handler instanceof TemplateRecipeHandler)) {
            return Collections.emptyList();
        }
        TemplateRecipeHandler template = (TemplateRecipeHandler) handler;
        if (recipeIndex < 0 || recipeIndex >= template.arecipes.size()) {
            return Collections.emptyList();
        }
        TemplateRecipeHandler.CachedRecipe cached = template.arecipes.get(recipeIndex);
        if (!(cached instanceof RecipeHandlerBase.CachedBaseRecipe)) {
            return Collections.emptyList();
        }
        List<FluidTankElement> tanks = ((RecipeHandlerBase.CachedBaseRecipe) cached).getFluidTanks();
        return tanks == null ? Collections.<FluidTankElement>emptyList() : tanks;
    }
}
