package moe.takochan.webnei.exporter.command;

import net.minecraft.command.ICommandSender;

import moe.takochan.webnei.exporter.export.ExportJobRunner;
import moe.takochan.webnei.exporter.export.ExportRequest;
import moe.takochan.webnei.exporter.export.listener.ChatExportJobListener;
import moe.takochan.webnei.exporter.workflow.HandlerScanWorkflow;

final class HandlerScanCommand implements ExportSubcommand {

    @Override
    public String name() {
        return "handlers";
    }

    @Override
    public String descriptionKey() {
        return "webnei.command.help.export.handlers";
    }

    @Override
    public void run(ICommandSender sender) {
        ExportJobRunner.defaults()
            .submit(ExportRequest.single(HandlerScanWorkflow.ID), new ChatExportJobListener(sender));
    }
}
