package moe.takochan.webnei.exporter.export;

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

    /** 当前计划包含的 step 总数。 */
    private final int totalSteps;

    /** 已完成 step 数量。 */
    private final int completedSteps;

    /** 当前正在执行的 step ID。 */
    private final String currentStepId;

    /** 当前正在执行的 step 本地化文案 key。 */
    private final String currentStepLabelKey;

    /** bundle writer 最终产生的输出文件路径。 */
    private final List<String> outputFiles;

    /** 失败原因；非失败状态下为空。 */
    private final String errorMessage;

    ExportJobSnapshot(long jobId, ExportJobState state, int totalSteps, int completedSteps, String currentStepId,
        String currentStepLabelKey, List<String> outputFiles, String errorMessage) {
        this.jobId = jobId;
        this.state = state;
        this.totalSteps = totalSteps;
        this.completedSteps = completedSteps;
        this.currentStepId = currentStepId;
        this.currentStepLabelKey = currentStepLabelKey;
        this.outputFiles = Collections.unmodifiableList(outputFiles);
        this.errorMessage = errorMessage;
    }
}
