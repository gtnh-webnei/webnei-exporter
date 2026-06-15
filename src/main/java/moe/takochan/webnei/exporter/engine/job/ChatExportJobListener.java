package moe.takochan.webnei.exporter.engine.job;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;

/** 把导出进度事件发送到 Minecraft 聊天栏。 */
public final class ChatExportJobListener implements IExportJobListener {

    /** 命令发送者。 */
    private final ICommandSender sender;

    public ChatExportJobListener(ICommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void onStarted(ExportJobSnapshot snapshot) {
        send("webnei.command.export.started", Integer.toString(snapshot.getTotalTasks()));
    }

    @Override
    public void onTaskStarted(ExportJobSnapshot snapshot) {
        send("webnei.command.export.task.started", taskName(snapshot));
    }

    @Override
    public void onTaskFinished(ExportJobSnapshot snapshot) {
        send("webnei.command.export.task.finished", taskName(snapshot));
    }

    @Override
    public void onFinished(ExportJobSnapshot snapshot) {
        send("webnei.command.export.finished", outputSummary(snapshot));
    }

    @Override
    public void onFailed(ExportJobSnapshot snapshot) {
        send("webnei.command.export.failed", snapshot.getErrorMessage());
    }

    private void send(String key, Object... args) {
        sender.addChatMessage(new ChatComponentTranslation(key, args));
    }

    private static String outputSummary(ExportJobSnapshot snapshot) {
        if (snapshot.getOutputFiles()
            .isEmpty()) {
            return "";
        }
        if (snapshot.getOutputFiles()
            .size() == 1) {
            return snapshot.getOutputFiles()
                .get(0);
        }
        return snapshot.getOutputFiles()
            .toString();
    }

    private static String taskName(ExportJobSnapshot snapshot) {
        if (snapshot.getCurrentTaskLabelKey() == null || snapshot.getCurrentTaskLabelKey()
            .isEmpty()) {
            return snapshot.getCurrentTaskId();
        }
        return StatCollector.translateToLocal(snapshot.getCurrentTaskLabelKey());
    }
}
