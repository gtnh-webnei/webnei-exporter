package moe.takochan.webnei.exporter.engine.job;

import java.util.ArrayList;
import java.util.List;

import moe.takochan.webnei.exporter.engine.task.IExportTask;

/**
 * 导出 job 的统一状态源。
 *
 * <p>
 * chat listener 和未来 GUI 都应读取同一个 session/snapshot。
 */
public final class ExportJobSession {

    private final long jobId;
    private final int totalTasks;
    private ExportJobState state = ExportJobState.PENDING;
    private int completedTasks;
    private String currentTaskId = "";
    private String currentTaskLabelKey = "";
    private final List<String> outputFiles = new ArrayList<>();
    private String errorMessage = "";

    public ExportJobSession(long jobId, int totalTasks) {
        this.jobId = jobId;
        this.totalTasks = totalTasks;
    }

    /** 标记 job 开始执行。 */
    public synchronized void start() {
        state = ExportJobState.RUNNING;
    }

    /** 记录当前正在执行的 task，用于主动进度提示。 */
    public synchronized void startTask(IExportTask task) {
        currentTaskId = task.id();
        currentTaskLabelKey = task.labelKey();
    }

    /** 标记一个 task 完成。 */
    public synchronized void finishTask() {
        completedTasks++;
    }

    /** 记录最终 bundle writer 产生的输出文件。 */
    public synchronized void finishBundle(List<String> outputs) {
        outputFiles.addAll(outputs);
    }

    /** 标记 job 成功结束。 */
    public synchronized void finish() {
        state = ExportJobState.DONE;
        currentTaskId = "";
        currentTaskLabelKey = "";
    }

    /** 标记 job 失败并记录原因。 */
    public synchronized void fail(String errorMessage) {
        state = ExportJobState.ERROR;
        this.errorMessage = errorMessage == null ? "" : errorMessage;
    }

    public synchronized ExportJobSnapshot snapshot() {
        return new ExportJobSnapshot(
            jobId,
            state,
            totalTasks,
            completedTasks,
            currentTaskId,
            currentTaskLabelKey,
            new ArrayList<>(outputFiles),
            errorMessage);
    }
}
