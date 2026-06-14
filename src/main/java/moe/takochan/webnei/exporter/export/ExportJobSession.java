package moe.takochan.webnei.exporter.export;

import java.util.ArrayList;
import java.util.List;

import moe.takochan.webnei.exporter.step.IExportStep;

/**
 * 导出 job 的统一状态源。
 *
 * <p>
 * chat listener 和未来 GUI 都应读取同一个 session/snapshot。
 * 现有 snapshot 字段名仍保留 workflow 命名以减少牵连，但当前语义已经是“plan 中的 step 进度”。
 */
public final class ExportJobSession {

    private final long jobId;
    private final int totalWorkflows;
    private ExportJobState state = ExportJobState.PENDING;
    private int completedWorkflows;
    private String currentWorkflowId = "";
    private String currentWorkflowLabelKey = "";
    private final List<String> outputFiles = new ArrayList<>();
    private String errorMessage = "";

    ExportJobSession(long jobId, int totalWorkflows) {
        this.jobId = jobId;
        this.totalWorkflows = totalWorkflows;
    }

    synchronized void start() {
        state = ExportJobState.RUNNING;
    }

    /** 记录当前正在执行的 step，用于主动进度提示。 */
    public synchronized void startStep(IExportStep step) {
        currentWorkflowId = step.id();
        currentWorkflowLabelKey = step.labelKey();
    }

    /** 标记一个 step 完成。 */
    public synchronized void finishStep() {
        completedWorkflows++;
    }

    /** 记录最终 bundle writer 产生的输出文件。 */
    synchronized void finishWorkflow(List<String> outputs) {
        outputFiles.addAll(outputs);
    }

    synchronized void finish() {
        state = ExportJobState.DONE;
        currentWorkflowId = "";
        currentWorkflowLabelKey = "";
    }

    synchronized void fail(String errorMessage) {
        state = ExportJobState.ERROR;
        this.errorMessage = errorMessage == null ? "" : errorMessage;
    }

    public synchronized ExportJobSnapshot snapshot() {
        return new ExportJobSnapshot(
            jobId,
            state,
            totalWorkflows,
            completedWorkflows,
            currentWorkflowId,
            currentWorkflowLabelKey,
            new ArrayList<>(outputFiles),
            errorMessage);
    }
}
