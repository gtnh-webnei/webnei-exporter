package moe.takochan.webnei.exporter.command;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.command.ICommandSender;

import moe.takochan.webnei.exporter.export.ExportJobRunner;
import moe.takochan.webnei.exporter.export.ExportRequest;
import moe.takochan.webnei.exporter.export.ExportRequestOptions;
import moe.takochan.webnei.exporter.export.listener.ChatExportJobListener;
import moe.takochan.webnei.exporter.plan.ExportPlanIds;

/** 第一阶段验证命令：请求 dataset / mod 基础数据导出计划。 */
final class DatasetModCommand implements ExportSubcommand {

    static final String DEFAULT_VARIANT = "official";

    @Override
    public String name() {
        return "dataset";
    }

    @Override
    public String descriptionKey() {
        return "webnei.command.help.export.dataset";
    }

    @Override
    public void run(ICommandSender sender, String[] args) {
        if (args.length < 2 || args.length > 3) {
            CommandMessages.send(sender, "webnei.command.usage.export.dataset");
            return;
        }
        Map<String, String> options = new LinkedHashMap<>();
        options.put(ExportRequestOptions.PACK_SLUG, args[0]);
        options.put(ExportRequestOptions.PACK_VERSION, args[1]);
        options.put(ExportRequestOptions.VARIANT, args.length == 3 ? args[2] : DEFAULT_VARIANT);
        ExportJobRunner.defaults()
            .submit(
                ExportRequest.plan(ExportPlanIds.DATASET_MOD_VALIDATION, options),
                new ChatExportJobListener(sender));
    }
}
