package moe.takochan.webnei.exporter.command;

import net.minecraft.command.ICommandSender;

import moe.takochan.webnei.exporter.export.ExportJobRunner;
import moe.takochan.webnei.exporter.export.ExportRequest;
import moe.takochan.webnei.exporter.export.listener.ChatExportJobListener;
import moe.takochan.webnei.exporter.plan.ExportPlanIds;

/** 临时验证命令：请求 handler/category 发现计划。 */
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
            .submit(ExportRequest.plan(ExportPlanIds.HANDLER_DISCOVERY_VALIDATION), new ChatExportJobListener(sender));
    }
}
