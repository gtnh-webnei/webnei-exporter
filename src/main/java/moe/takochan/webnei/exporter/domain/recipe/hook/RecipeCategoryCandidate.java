package moe.takochan.webnei.exporter.domain.recipe.hook;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class RecipeCategoryCandidate {

    /** 由 handler 身份确定性派生的分类 ID，与 recipe_category 导出/主键一致。用于排除匹配。 */
    private final String categoryId;

    /** 分类归属 mod id，仅供展示。 */
    private final String modId;

    /** 分类显示名，仅供展示。 */
    private final String name;
}
