package moe.takochan.webnei.exporter.export;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import moe.takochan.webnei.exporter.workflow.HandlerScanWorkflow;
import moe.takochan.webnei.exporter.workflow.SlotExtractionWorkflow;

public final class ExportWorkflowRegistry {

    private final Map<String, IExportWorkflow> workflows;

    public ExportWorkflowRegistry(Iterable<IExportWorkflow> workflows) {
        Map<String, IExportWorkflow> byId = new LinkedHashMap<>();
        for (IExportWorkflow workflow : workflows) {
            byId.put(workflow.id(), workflow);
        }
        this.workflows = Collections.unmodifiableMap(byId);
    }

    public static ExportWorkflowRegistry defaults() {
        return new ExportWorkflowRegistry(
            Arrays.<IExportWorkflow>asList(new HandlerScanWorkflow(), new SlotExtractionWorkflow()));
    }

    public IExportWorkflow get(String id) {
        return workflows.get(id);
    }
}
