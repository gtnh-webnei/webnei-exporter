package moe.takochan.webnei.exporter.export.listener;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;

import moe.takochan.webnei.exporter.bundle.BundleResult;
import moe.takochan.webnei.exporter.export.ExportJobSnapshot;
import moe.takochan.webnei.exporter.export.IExportJobListener;

public final class ChatExportJobListener implements IExportJobListener {

    private final ICommandSender sender;

    public ChatExportJobListener(ICommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void onStarted(ExportJobSnapshot snapshot) {
        send("webnei.command.export.started", Integer.toString(snapshot.totalWorkflows));
    }

    @Override
    public void onWorkflowStarted(ExportJobSnapshot snapshot) {
        send("webnei.command.export.task.started", workflowName(snapshot));
    }

    @Override
    public void onWorkflowFinished(ExportJobSnapshot snapshot, BundleResult result) {
        send("webnei.command.export.task.finished", workflowName(snapshot), result.outputSummary());
    }

    @Override
    public void onFinished(ExportJobSnapshot snapshot) {
        send("webnei.command.export.finished");
    }

    @Override
    public void onFailed(ExportJobSnapshot snapshot) {
        send("webnei.command.export.failed", snapshot.errorMessage);
    }

    private void send(String key, Object... args) {
        sender.addChatMessage(new ChatComponentTranslation(key, args));
    }

    private static String workflowName(ExportJobSnapshot snapshot) {
        if (snapshot.currentWorkflowLabelKey == null || snapshot.currentWorkflowLabelKey.isEmpty()) {
            return snapshot.currentWorkflowId;
        }
        return StatCollector.translateToLocal(snapshot.currentWorkflowLabelKey);
    }
}
