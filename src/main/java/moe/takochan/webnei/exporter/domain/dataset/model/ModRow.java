package moe.takochan.webnei.exporter.domain.dataset.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** mod 表行：当前已加载 mod 的只读快照。 */
@Getter
@RequiredArgsConstructor
public final class ModRow {

    /** 所属 dataset ID。 */
    private final String datasetId;

    /** Forge mod id。 */
    private final String modId;

    /** ModContainer 暴露的显示名称。 */
    private final String name;

    /** ModContainer 暴露的版本号。 */
    private final String version;

    /** mod 来源类型：file、directory 或 unknown。 */
    private final String sourceType;

    /** mod 来源文件或目录名称。 */
    private final String sourceFileName;

    /** mod 来源文件 SHA-256；目录或未知来源为空。 */
    private final String sourceSha256;

    /** 当前导出时该 mod 是否启用。 */
    private final boolean enabled;
}
