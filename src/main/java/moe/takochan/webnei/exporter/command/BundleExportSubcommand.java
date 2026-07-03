package moe.takochan.webnei.exporter.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;

import moe.takochan.webnei.exporter.bundle.BundleFormat;
import moe.takochan.webnei.exporter.client.gui.ExportGuiLauncher;
import moe.takochan.webnei.exporter.engine.ExportRequest;
import moe.takochan.webnei.exporter.engine.ExportRequestOptions;
import moe.takochan.webnei.exporter.export.ExportPlan;

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
        ExportGuiLauncher.submitAndShowProgress(ExportRequest.bundle(planId, arguments.format(), arguments.options()));
    }

    /**
     * 解析后的 bundle 导出参数。
     */
    private static final class BundleArguments {

        /** 选项 flag：不渲染图片资源。 */
        private static final String FLAG_NO_IMAGES = "--no-images";

        /** 选项 flag：动图退化为静态首帧。 */
        private static final String FLAG_NO_ANIMATIONS = "--no-animations";

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
         * 解析 pack、variant、可选 format 与可选 flag 参数。
         *
         * <p>
         * 位置参数：{@code pack_slug pack_version variant [format]}；位置参数之后可附加任意顺序的 flag：
         * {@code --no-images}（不导图片资源）、{@code --no-animations}（不导动图序列）。
         */
        private static BundleArguments parse(String[] args) {
            List<String> positional = new ArrayList<>();
            boolean noImages = false;
            boolean noAnimations = false;
            for (String arg : args) {
                if (arg == null) {
                    continue;
                }
                if (FLAG_NO_IMAGES.equalsIgnoreCase(arg)) {
                    noImages = true;
                } else if (FLAG_NO_ANIMATIONS.equalsIgnoreCase(arg)) {
                    noAnimations = true;
                } else if (arg.startsWith("--")) {
                    throw new IllegalArgumentException("Unknown flag: " + arg);
                } else {
                    positional.add(arg);
                }
            }
            if (positional.size() < 3 || positional.size() > 4) {
                throw new IllegalArgumentException("Expected pack_slug, pack_version, variant and optional format");
            }
            Map<String, String> options = new LinkedHashMap<>();
            options.put(ExportRequestOptions.PACK_SLUG, positional.get(0));
            options.put(ExportRequestOptions.PACK_VERSION, positional.get(1));
            options.put(ExportRequestOptions.VARIANT, positional.get(2));
            if (noImages) {
                options.put(ExportRequestOptions.SKIP_ASSET_RENDER, "true");
            }
            if (noAnimations) {
                options.put(ExportRequestOptions.SKIP_ASSET_ANIMATIONS, "true");
            }
            return new BundleArguments(options, format(positional));
        }

        private Map<String, String> options() {
            return options;
        }

        private BundleFormat format() {
            return format;
        }

        private static BundleFormat format(List<String> positional) {
            if (positional.size() == 4) {
                return BundleFormat.parse(positional.get(3));
            }
            return BundleFormat.defaultFormat();
        }
    }
}
