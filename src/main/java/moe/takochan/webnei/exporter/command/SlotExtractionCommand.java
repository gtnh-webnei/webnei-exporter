package moe.takochan.webnei.exporter.command;

import net.minecraft.command.ICommandSender;

import moe.takochan.webnei.exporter.export.ExportJobRunner;
import moe.takochan.webnei.exporter.export.ExportRequest;
import moe.takochan.webnei.exporter.export.listener.ChatExportJobListener;
import moe.takochan.webnei.exporter.plan.ExportPlanIds;

/** 临时验证命令：请求 recipe visual facts 验证计划，后续会被正式导出计划替代。 */
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
    public void run(ICommandSender sender, String[] args) {
        ExportJobRunner.defaults()
            .submit(
                ExportRequest.plan(ExportPlanIds.RECIPE_VISUAL_FACTS_VALIDATION),
                new ChatExportJobListener(sender));
    }
}
