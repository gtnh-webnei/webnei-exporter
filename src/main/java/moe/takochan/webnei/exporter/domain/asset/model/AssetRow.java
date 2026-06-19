package moe.takochan.webnei.exporter.domain.asset.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** asset 表行：资源文件与其 owning domain 对象的最小关联。 */
@Getter
@RequiredArgsConstructor
public final class AssetRow {

    /** 所属 dataset ID。 */
    private final String datasetId;

    /** owner domain 类型，例如 item_variant、fluid。 */
    private final String ownerType;

    /** owner 稳定 ID。 */
    private final String ownerId;

    /** asset 种类，例如 item_icon、fluid_icon。 */
    private final String kind;

    /** bundle 内资源相对路径。 */
    private final String path;

    /** 资源文件 SHA-256；尚未渲染文件时为空。 */
    private final String sha256;

    /** 图片宽度。 */
    private final int width;

    /** 图片高度。 */
    private final int height;
}
