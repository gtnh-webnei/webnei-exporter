package moe.takochan.webnei.exporter.command;

import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public final class WebneiCommand implements ICommand {

    private final ExportCommandRouter router = new ExportCommandRouter();

    @Override
    public String getCommandName() {
        return "webnei";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "webnei.command.usage";
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!router.run(sender, args)) {
            router.sendUsage(sender);
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return router.complete(args);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(Object other) {
        if (other instanceof ICommand) {
            return getCommandName().compareTo(((ICommand) other).getCommandName());
        }
        return 0;
    }
}
