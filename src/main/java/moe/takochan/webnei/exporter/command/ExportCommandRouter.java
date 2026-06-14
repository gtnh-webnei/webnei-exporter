package moe.takochan.webnei.exporter.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.command.ICommandSender;

final class ExportCommandRouter {

    private final List<ExportSubcommand> subcommands = Arrays
        .asList(new DatasetModCommand(), new HandlerScanCommand(), new SlotExtractionCommand());

    boolean run(ICommandSender sender, String[] args) {
        if (args.length == 0 || isHelp(args)) {
            sendHelp(sender);
            return true;
        }
        if (args.length < 2 || !"export".equals(args[0])) {
            return false;
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

    @SuppressWarnings("rawtypes")
    List complete(String[] args) {
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

    void sendUsage(ICommandSender sender) {
        CommandMessages.send(sender, "webnei.command.usage");
    }

    private void sendHelp(ICommandSender sender) {
        CommandMessages.send(sender, "webnei.command.help.header");
        CommandMessages.send(sender, "webnei.command.help.help");
        for (ExportSubcommand subcommand : subcommands) {
            CommandMessages.send(sender, subcommand.descriptionKey());
        }
    }

    private static boolean isHelp(String[] args) {
        return args.length == 1 && "help".equals(args[0])
            || args.length == 2 && "export".equals(args[0]) && "help".equals(args[1]);
    }

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
