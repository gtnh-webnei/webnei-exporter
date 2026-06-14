package moe.takochan.webnei.exporter.export;

import java.util.Collections;
import java.util.List;

public final class ExportJobSnapshot {

    public final long jobId;
    public final ExportJobState state;
    public final int totalWorkflows;
    public final int completedWorkflows;
    public final String currentWorkflowId;
    public final String currentWorkflowLabelKey;
    public final List<String> outputFiles;
    public final String errorMessage;

    ExportJobSnapshot(long jobId, ExportJobState state, int totalWorkflows, int completedWorkflows,
        String currentWorkflowId, String currentWorkflowLabelKey, List<String> outputFiles, String errorMessage) {
        this.jobId = jobId;
        this.state = state;
        this.totalWorkflows = totalWorkflows;
        this.completedWorkflows = completedWorkflows;
        this.currentWorkflowId = currentWorkflowId;
        this.currentWorkflowLabelKey = currentWorkflowLabelKey;
        this.outputFiles = Collections.unmodifiableList(outputFiles);
        this.errorMessage = errorMessage;
    }
}
