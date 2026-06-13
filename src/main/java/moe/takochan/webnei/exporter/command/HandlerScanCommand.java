package moe.takochan.webnei.exporter.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import moe.takochan.webnei.exporter.bundle.BundleResult;
import moe.takochan.webnei.exporter.workflow.HandlerScanWorkflow;

final class HandlerScanCommand implements ExportSubcommand {

    private final HandlerScanWorkflow workflow = new HandlerScanWorkflow();

    @Override
    public String name() {
        return "handlers";
    }

    @Override
    public void run(ICommandSender sender) {
        BundleResult result = workflow.run();
        if (result.success) {
            sender.addChatMessage(new ChatComponentText("WebNEI handler scan bundle: " + result.outputSummary()));
        } else {
            sender
                .addChatMessage(new ChatComponentText("Failed to create handler scan bundle: " + result.errorMessage));
        }
    }
}
