package moe.takochan.webnei.exporter.engine.job;

/** 导出阶段（task 或 bundle 写出）的进度状态。 */
public enum ExportPhaseState {

    /** 尚未开始。 */
    PENDING,

    /** 正在执行。 */
    RUNNING,

    /** 已完成。 */
    DONE
}
