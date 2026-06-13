package moe.takochan.webnei.exporter;

import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;

public final class WebneiCommand implements ICommand {

    @Override
    public String getCommandName() {
        return "webnei";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/webnei export smoke";
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 2 && "export".equals(args[0]) && "smoke".equals(args[1])) {
            int craftingHandlers = GuiCraftingRecipe.craftinghandlers.size();
            int serialCraftingHandlers = GuiCraftingRecipe.serialCraftingHandlers.size();
            int usageHandlers = GuiUsageRecipe.usagehandlers.size();
            int serialUsageHandlers = GuiUsageRecipe.serialUsageHandlers.size();
            sender.addChatMessage(new ChatComponentText("WebNEI Exporter smoke check"));
            sender.addChatMessage(
                new ChatComponentText(
                    "craftinghandlers=" + craftingHandlers
                        + ", serialCraftingHandlers="
                        + serialCraftingHandlers
                        + ", usagehandlers="
                        + usageHandlers
                        + ", serialUsageHandlers="
                        + serialUsageHandlers));
            WebneiExporter.LOG.info(
                "WebNEI Exporter smoke: craftingHandlers={}, serialCraftingHandlers={}, usageHandlers={}, serialUsageHandlers={}",
                craftingHandlers,
                serialCraftingHandlers,
                usageHandlers,
                serialUsageHandlers);
            return;
        }
        sender.addChatMessage(new ChatComponentText("Usage: " + getCommandUsage(sender)));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return null;
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
