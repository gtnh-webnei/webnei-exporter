package moe.takochan.webnei.exporter.domain.asset.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** asset 表行：资源文件与其 owning domain 对象的关联。 */
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

    /** MIME 类型。 */
    private final String mediaType;

    /** 图片宽度。 */
    private final int width;

    /** 图片高度。 */
    private final int height;

    /** 扩展元信息 JSON；静态图标为 {}。 */
    private final String metadataJson;
}
