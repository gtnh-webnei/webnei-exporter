package moe.takochan.webnei.exporter.engine.job;

/** 导出 job 生命周期状态。 */
public enum ExportJobState {

    /** 已创建但尚未开始执行。 */
    PENDING,

    /** 正在执行。 */
    RUNNING,

    /** 已成功完成。 */
    DONE,

    /** 执行失败。 */
    ERROR
}
