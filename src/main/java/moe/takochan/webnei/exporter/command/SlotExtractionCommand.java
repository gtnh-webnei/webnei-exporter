package moe.takochan.webnei.exporter.command;

import net.minecraft.command.ICommandSender;

import moe.takochan.webnei.exporter.export.ExportJobRunner;
import moe.takochan.webnei.exporter.export.ExportRequest;
import moe.takochan.webnei.exporter.export.listener.ChatExportJobListener;
import moe.takochan.webnei.exporter.workflow.SlotExtractionWorkflow;

final class SlotExtractionCommand implements ExportSubcommand {

    @Override
    public String name() {
        return "slots";
    }

    @Override
    public String descriptionKey() {
        return "webnei.command.help.export.slots";
    }

    @Override
    public void run(ICommandSender sender) {
        ExportJobRunner.defaults()
            .submit(ExportRequest.single(SlotExtractionWorkflow.ID), new ChatExportJobListener(sender));
    }
}
