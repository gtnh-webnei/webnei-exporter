package moe.takochan.webnei.exporter.command;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

final class ExportCommandRouter {

    private final List<ExportSubcommand> subcommands = Arrays.asList(new HandlerScanCommand());

    boolean run(ICommandSender sender, String[] args) {
        if (args.length != 2 || !"export".equals(args[0])) {
            return false;
        }
        for (ExportSubcommand subcommand : subcommands) {
            if (subcommand.name()
                .equals(args[1])) {
                subcommand.run(sender);
                return true;
            }
        }
        return false;
    }

    void sendUsage(ICommandSender sender, String usage) {
        sender.addChatMessage(new ChatComponentText("Usage: " + usage));
    }
}
