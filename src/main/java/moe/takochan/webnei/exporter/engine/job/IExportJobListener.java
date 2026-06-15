package moe.takochan.webnei.exporter.engine.job;

/** 导出 job 进度事件监听器。 */
public interface IExportJobListener {

    /** job 已创建并开始执行。 */
    void onStarted(ExportJobSnapshot snapshot);

    /** 当前 task 已开始。 */
    void onTaskStarted(ExportJobSnapshot snapshot);

    /** 当前 task 已完成；bundle 此时尚未写出。 */
    void onTaskFinished(ExportJobSnapshot snapshot);

    /** 所有 task 和 bundle 写出均已完成。 */
    void onFinished(ExportJobSnapshot snapshot);

    /** job 执行失败。 */
    void onFailed(ExportJobSnapshot snapshot);
}
