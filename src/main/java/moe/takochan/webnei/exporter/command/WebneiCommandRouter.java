package moe.takochan.webnei.exporter.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.command.ICommandSender;

import moe.takochan.webnei.exporter.client.gui.ExportGuiLauncher;

/**
 * /webnei 命令路由器，负责帮助、补全和导出子命令分发。
 */
public final class WebneiCommandRouter {

    /**
     * 当前已注册的导出子命令，顺序也用于帮助展示和补全。
     */
    private final List<ExportSubcommand> subcommands = Arrays.asList(new AllExportSubcommand());

    /**
     * 解析并执行 /webnei 参数；无法处理时返回未处理状态，由入口命令显示总用法。
     */
    public boolean run(ICommandSender sender, String[] args) {
        if (args.length == 0 || isHelp(args)) {
            sendHelp(sender);
            return true;
        }
        if (!"export".equals(args[0])) {
            return false;
        }
        if (args.length == 1) {
            ExportGuiLauncher.showConfig();
            return true;
        }
        for (ExportSubcommand subcommand : subcommands) {
            if (subcommand.name()
                .equals(args[1])) {
                subcommand.run(sender, Arrays.copyOfRange(args, 2, args.length));
                return true;
            }
        }
        return false;
    }

    /**
     * 生成当前命令层级的补全候选。
     */
    public List<String> complete(String[] args) {
        if (args.length == 1) {
            return matching(args[0], Arrays.asList("help", "export"));
        }
        if (args.length == 2 && "export".equals(args[0])) {
            List<String> values = new ArrayList<>();
            values.add("help");
            for (ExportSubcommand subcommand : subcommands) {
                values.add(subcommand.name());
            }
            return matching(args[1], values);
        }
        return Collections.emptyList();
    }

    /**
     * 发送根命令用法。
     */
    public void sendUsage(ICommandSender sender) {
        CommandMessageSender.send(sender, "webnei.command.usage");
    }

    /**
     * 发送帮助头和所有导出子命令说明。
     */
    private void sendHelp(ICommandSender sender) {
        CommandMessageSender.send(sender, "webnei.command.help.header");
        CommandMessageSender.send(sender, "webnei.command.help.help");
        CommandMessageSender.send(sender, "webnei.command.help.export.gui");
        for (ExportSubcommand subcommand : subcommands) {
            CommandMessageSender.send(sender, subcommand.descriptionKey());
        }
    }

    /**
     * 判断参数是否请求帮助。
     */
    private static boolean isHelp(String[] args) {
        return args.length == 1 && "help".equals(args[0])
            || args.length == 2 && "export".equals(args[0]) && "help".equals(args[1]);
    }

    /**
     * 按前缀过滤补全候选。
     */
    private static List<String> matching(String prefix, List<String> values) {
        List<String> matches = new ArrayList<>();
        for (String value : values) {
            if (value.startsWith(prefix)) {
                matches.add(value);
            }
        }
        return matches;
    }
}
