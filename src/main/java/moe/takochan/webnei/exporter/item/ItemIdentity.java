package moe.takochan.webnei.exporter.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** registry item 维度的稳定身份，是 ItemStack identity 的第一部分。 */
@Getter
@RequiredArgsConstructor
public final class ItemIdentity {

    /** registry item 稳定 ID，通常为 modid:name。 */
    private final String itemId;

    /** item 所属 mod id。 */
    private final String modId;

    /** Forge registry name 的本地名称部分。 */
    private final String registryName;
}
