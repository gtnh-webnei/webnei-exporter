package moe.takochan.webnei.exporter.export;

import moe.takochan.webnei.exporter.bundle.BundleResult;

public interface IExportJobListener {

    void onStarted(ExportJobSnapshot snapshot);

    void onWorkflowStarted(ExportJobSnapshot snapshot);

    void onWorkflowFinished(ExportJobSnapshot snapshot, BundleResult result);

    void onFinished(ExportJobSnapshot snapshot);

    void onFailed(ExportJobSnapshot snapshot);
}
