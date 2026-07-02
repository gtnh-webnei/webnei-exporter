package moe.takochan.webnei.exporter.domain.recipe.internal;

import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 一个配方页面的 NEI visual facts 观察值。
 *
 * <p>
 * NEI 显示规则：
 * <ul>
 * <li>ingredients 永远是输入。</li>
 * <li>有 result 时，result 是输出，others 是辅助格。</li>
 * <li>没有 result 时，others 充当输出，inputs/auxiliary 字段拆分由 registrar 决定。</li>
 * </ul>
 *
 * <p>
 * {@code extraInputs} / {@code extraOutputs} 是格子来源钩子（{@code IRecipeSlotSourceHook}）从非标准来源补充的格子，
 * role 已由钩子明确指定，registrar 直接并入对应 role，不参与 result/others 的拆分推断。
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class RecipeVisualObservation {

    /** 输入格集合，可能为空。 */
    private final List<RecipeSlotObservation> inputs;

    /** 主输出格；可能为 null。 */
    private final RecipeSlotObservation result;

    /** 额外格集合；有 result 时是辅助格，没 result 时充当输出格。 */
    private final List<RecipeSlotObservation> others;

    /** 钩子补充的、role 明确为输入的额外格子。 */
    private final List<RecipeSlotObservation> extraInputs;

    /** 钩子补充的、role 明确为输出的额外格子。 */
    private final List<RecipeSlotObservation> extraOutputs;

    static RecipeVisualObservation of(List<RecipeSlotObservation> inputs, RecipeSlotObservation result,
        List<RecipeSlotObservation> others, List<RecipeSlotObservation> extraInputs,
        List<RecipeSlotObservation> extraOutputs) {
        return new RecipeVisualObservation(
            Collections.unmodifiableList(inputs),
            result,
            Collections.unmodifiableList(others),
            Collections.unmodifiableList(extraInputs),
            Collections.unmodifiableList(extraOutputs));
    }
}
