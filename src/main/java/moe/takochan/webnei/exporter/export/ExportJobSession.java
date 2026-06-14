package moe.takochan.webnei.exporter.export;

import java.util.ArrayList;
import java.util.List;

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

    synchronized void startWorkflow(IExportWorkflow workflow) {
        currentWorkflowId = workflow.id();
        currentWorkflowLabelKey = workflow.labelKey();
    }

    synchronized void finishWorkflow(List<String> outputs) {
        completedWorkflows++;
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
