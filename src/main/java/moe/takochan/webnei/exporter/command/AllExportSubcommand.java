package moe.takochan.webnei.exporter.command;

import moe.takochan.webnei.exporter.export.ExportPlan;

/**
 * 当前对外保留的统一导出命令。
 */
public final class AllExportSubcommand extends BundleExportSubcommand {

    /**
     * 命令名。
     */
    private static final String NAME = "all";

    /**
     * 帮助文案本地化键。
     */
    private static final String HELP_KEY = "webnei.command.help.export.all";

    /**
     * 用法文案本地化键。
     */
    private static final String USAGE_KEY = "webnei.command.usage.export.all";

    public AllExportSubcommand() {
        super(NAME, HELP_KEY, USAGE_KEY, ExportPlan.ALL);
    }
}
