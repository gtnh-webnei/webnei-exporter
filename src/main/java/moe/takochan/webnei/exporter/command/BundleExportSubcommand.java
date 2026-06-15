package moe.takochan.webnei.exporter.command;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import moe.takochan.webnei.exporter.export.ExportPlan;
import net.minecraft.command.ICommandSender;

import moe.takochan.webnei.exporter.bundle.BundleFormat;
import moe.takochan.webnei.exporter.engine.ExportRequest;
import moe.takochan.webnei.exporter.engine.ExportRequestOptions;
import moe.takochan.webnei.exporter.engine.job.ChatExportJobListener;
import moe.takochan.webnei.exporter.engine.job.ExportJobRunner;

/**
 * 需要 pack、variant 和 bundle format 参数的导出子命令基类。
 */
public abstract class BundleExportSubcommand implements ExportSubcommand {

    /**
     * 子命令名称。
     */
    private final String name;

    /**
     * 帮助文案本地化键。
     */
    private final String descriptionKey;

    /**
     * 用法文案本地化键。
     */
    private final String usageKey;

    /**
     * 子命令提交的导出计划 ID。
     */
    private final String planId;

    protected BundleExportSubcommand(String name, String descriptionKey, String usageKey, ExportPlan plan) {
        this.name = name;
        this.descriptionKey = descriptionKey;
        this.usageKey = usageKey;
        this.planId = plan.getId();
    }

    @Override
    public final String name() {
        return name;
    }

    @Override
    public final String descriptionKey() {
        return descriptionKey;
    }

    @Override
    public final void run(ICommandSender sender, String[] args) {
        BundleArguments arguments;
        try {
            arguments = BundleArguments.parse(args);
        } catch (IllegalArgumentException e) {
            CommandMessageSender.send(sender, usageKey);
            return;
        }
        ExportJobRunner.defaults()
            .submit(
                ExportRequest.bundle(planId, arguments.format(), arguments.options()),
                new ChatExportJobListener(sender));
    }

    /**
     * 解析后的 bundle 导出参数。
     */
    private static final class BundleArguments {

        /**
         * 导出请求选项。
         */
        private final Map<String, String> options;

        /**
         * 用户指定或默认的输出格式。
         */
        private final BundleFormat format;

        private BundleArguments(Map<String, String> options, BundleFormat format) {
            this.options = Collections.unmodifiableMap(options);
            this.format = format;
        }

        /**
         * 解析 pack、variant 和 format 参数。
         */
        private static BundleArguments parse(String[] args) {
            if (args.length < 3 || args.length > 4) {
                throw new IllegalArgumentException("Expected pack_slug, pack_version, variant and optional format");
            }
            Map<String, String> options = new LinkedHashMap<>();
            options.put(ExportRequestOptions.PACK_SLUG, args[0]);
            options.put(ExportRequestOptions.PACK_VERSION, args[1]);
            options.put(ExportRequestOptions.VARIANT, args[2]);
            return new BundleArguments(options, format(args));
        }

        private Map<String, String> options() {
            return options;
        }

        private BundleFormat format() {
            return format;
        }

        private static BundleFormat format(String[] args) {
            if (args.length == 4) {
                return BundleFormat.parse(args[3]);
            }
            return BundleFormat.defaultFormat();
        }
    }
}
