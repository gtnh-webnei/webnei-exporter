package moe.takochan.webnei.exporter.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.StatCollector;

import moe.takochan.webnei.exporter.bundle.BundleResult;
import moe.takochan.webnei.exporter.export.ExportJobSnapshot;
import moe.takochan.webnei.exporter.export.IExportJobListener;

final class ChatExportJobListener implements IExportJobListener {

    private final ICommandSender sender;

    ChatExportJobListener(ICommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void onStarted(ExportJobSnapshot snapshot) {
        CommandMessages.send(sender, "webnei.command.export.started", Integer.toString(snapshot.totalWorkflows));
    }

    @Override
    public void onWorkflowStarted(ExportJobSnapshot snapshot) {
        CommandMessages.send(sender, "webnei.command.export.task.started", workflowName(snapshot));
    }

    @Override
    public void onWorkflowFinished(ExportJobSnapshot snapshot, BundleResult result) {
        CommandMessages
            .send(sender, "webnei.command.export.task.finished", workflowName(snapshot), result.outputSummary());
    }

    @Override
    public void onFinished(ExportJobSnapshot snapshot) {
        CommandMessages.send(sender, "webnei.command.export.finished");
    }

    @Override
    public void onFailed(ExportJobSnapshot snapshot) {
        CommandMessages.send(sender, "webnei.command.export.failed", snapshot.errorMessage);
    }

    private static String workflowName(ExportJobSnapshot snapshot) {
        if (snapshot.currentWorkflowLabelKey == null || snapshot.currentWorkflowLabelKey.isEmpty()) {
            return snapshot.currentWorkflowId;
        }
        return StatCollector.translateToLocal(snapshot.currentWorkflowLabelKey);
    }
}
