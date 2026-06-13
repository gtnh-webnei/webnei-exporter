package moe.takochan.webnei.exporter.command;

import net.minecraft.command.ICommandSender;

import moe.takochan.webnei.exporter.bundle.BundleResult;
import moe.takochan.webnei.exporter.workflow.HandlerScanWorkflow;

final class HandlerScanCommand implements ExportSubcommand {

    private final HandlerScanWorkflow workflow = new HandlerScanWorkflow();

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
        BundleResult result = workflow.run();
        if (result.success) {
            CommandMessages.send(sender, "webnei.command.bundle.handlers.success", result.outputSummary());
        } else {
            CommandMessages.send(sender, "webnei.command.bundle.handlers.failure", result.errorMessage);
        }
    }
}
