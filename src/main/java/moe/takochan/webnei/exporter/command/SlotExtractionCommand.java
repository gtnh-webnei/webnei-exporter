package moe.takochan.webnei.exporter.command;

import net.minecraft.command.ICommandSender;

import moe.takochan.webnei.exporter.bundle.BundleResult;
import moe.takochan.webnei.exporter.workflow.SlotExtractionWorkflow;

final class SlotExtractionCommand implements ExportSubcommand {

    private final SlotExtractionWorkflow workflow = new SlotExtractionWorkflow();

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
        BundleResult result = workflow.run();
        if (result.success) {
            CommandMessages.send(sender, "webnei.command.bundle.slots.success", result.outputSummary());
        } else {
            CommandMessages.send(sender, "webnei.command.bundle.slots.failure", result.errorMessage);
        }
    }
}
