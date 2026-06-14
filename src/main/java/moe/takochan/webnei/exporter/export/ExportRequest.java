package moe.takochan.webnei.exporter.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ExportRequest {

    private final List<String> workflowIds;

    private ExportRequest(List<String> workflowIds) {
        this.workflowIds = Collections.unmodifiableList(new ArrayList<>(workflowIds));
    }

    public static ExportRequest single(String workflowId) {
        return new ExportRequest(Collections.singletonList(workflowId));
    }

    public List<String> workflowIds() {
        return workflowIds;
    }
}
