package moe.takochan.webnei.exporter.domain.recipe.hook;

import java.util.Collections;
import java.util.List;

import net.minecraftforge.fluids.FluidStack;

import lombok.Getter;

/**
 * {@link IRecipeSlotSourceHook} 产出的一个额外格子：带 role 与坐标，候选以 Forge {@link FluidStack} 原始形式给出。
 *
 * <p>
 * 钩子不解析 fluid_id（不持有 fluid store），只交出 {@link FluidStack}，由抽取层用 fluid 域解析成稳定 fluid_id 与 amount。
 */
@Getter
public final class ExtraRecipeSlot {

    /** 格子在配方页面内的角色。 */
    public enum Role {
        INPUT,
        OUTPUT
    }

    private final Role role;
    private final int x;
    private final int y;
    private final List<FluidStack> fluidCandidates;

    public ExtraRecipeSlot(Role role, int x, int y, List<FluidStack> fluidCandidates) {
        this.role = role;
        this.x = x;
        this.y = y;
        this.fluidCandidates = Collections.unmodifiableList(fluidCandidates);
    }
}
