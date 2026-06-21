package moe.takochan.webnei.exporter.domain.asset.render;

/**
 * 资产渲染进度回调。
 *
 * <p>
 * 由渲染阶段在客户端线程周期性调用，把"已完成/总数"上报给上层（如导出 session），供 GUI 实时展示。
 * 渲染阶段不关心进度的具体去向。
 */
public interface AssetRenderProgress {

    /** 无操作实现，用于不需要进度上报的场景。 */
    AssetRenderProgress NONE = (done, total) -> {};

    void onProgress(int done, int total);
}
