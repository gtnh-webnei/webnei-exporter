package moe.takochan.webnei.exporter.export;

import moe.takochan.webnei.exporter.bundle.BundleResult;

public interface IExportWorkflow {

    String id();

    String labelKey();

    BundleResult execute(ExportExecutionContext context);
}
