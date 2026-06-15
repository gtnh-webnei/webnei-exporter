package moe.takochan.webnei.exporter.domain.dataset.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** dataset 表行：一次导出的全局身份。 */
@Getter
@RequiredArgsConstructor
public final class DatasetRow {

    /** dataset 主键，格式由 pack/version/variant/language 组合决定。 */
    private final String datasetId;

    /** 整合包短名称，例如 gtnh。 */
    private final String packSlug;

    /** 整合包版本号。 */
    private final String packVersion;

    /** 数据变体，例如 official、dev 或用户指定变体。 */
    private final String variant;

    /** 当前 Minecraft/Forge 语言代码。 */
    private final String language;

    /** 面向人类展示的 dataset 名称。 */
    private final String displayName;

    /** exporter 输出 schema 版本。 */
    private final String schemaVersion;

    /** exporter mod 自身版本。 */
    private final String exporterVersion;

    /** UTC 导出时间，ISO-8601 文本。 */
    private final String createdAt;

    /** 当前运行的 Minecraft 版本。 */
    private final String minecraftVersion;
}
