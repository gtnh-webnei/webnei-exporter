package moe.takochan.webnei.exporter.engine.job;

import java.util.Collections;
import java.util.List;

import lombok.Getter;

/** 导出 job 的只读状态快照，供 chat listener 和未来 GUI 展示进度。 */
@Getter
public final class ExportJobSnapshot {

    /** 本 JVM 内递增的 job ID。 */
    private final long jobId;

    /** 当前 job 状态。 */
    private final ExportJobState state;

    /** 当前计划包含的 task 总数。 */
    private final int totalTasks;

    /** 已完成 task 数量。 */
    private final int completedTasks;

    /** 当前正在执行的 task ID。 */
    private final String currentTaskId;

    /** 当前正在执行的 task 本地化文案 key。 */
    private final String currentTaskLabelKey;

    /** bundle writer 最终产生的输出文件路径。 */
    private final List<String> outputFiles;

    /** 失败原因；非失败状态下为空。 */
    private final String errorMessage;

    /** 当前阶段已完成的细粒度单元数（如已渲染图标数）。 */
    private final int renderDone;

    /** 当前阶段细粒度单元总数（如待渲染图标总数）；为 0 表示当前阶段无细粒度进度。 */
    private final int renderTotal;

    public ExportJobSnapshot(long jobId, ExportJobState state, int totalTasks, int completedTasks, String currentTaskId,
        String currentTaskLabelKey, List<String> outputFiles, String errorMessage, int renderDone, int renderTotal) {
        this.jobId = jobId;
        this.state = state;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.currentTaskId = currentTaskId;
        this.currentTaskLabelKey = currentTaskLabelKey;
        this.outputFiles = Collections.unmodifiableList(outputFiles);
        this.errorMessage = errorMessage;
        this.renderDone = renderDone;
        this.renderTotal = renderTotal;
    }
}
