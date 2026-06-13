package moe.takochan.webnei.exporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import moe.takochan.webnei.exporter.nei.HandlerScanRow;
import moe.takochan.webnei.exporter.nei.NeiHandlerScanner;

public final class WebneiCommand implements ICommand {

    @Override
    public String getCommandName() {
        return "webnei";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/webnei export smoke|handlers";
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
        if (args.length == 2 && "export".equals(args[0]) && "handlers".equals(args[1])) {
            try {
                File report = writeHandlersReport(new NeiHandlerScanner().scan());
                sender.addChatMessage(new ChatComponentText("WebNEI handler report: " + report.getAbsolutePath()));
            } catch (IOException e) {
                WebneiExporter.LOG.error("Failed to write WebNEI handler report", e);
                sender.addChatMessage(new ChatComponentText("Failed to write handler report: " + e.getMessage()));
            }
            return;
        }
        sender.addChatMessage(new ChatComponentText("Usage: " + getCommandUsage(sender)));
    }

    private File writeHandlersReport(List<HandlerScanRow> rows) throws IOException {
        File dir = new File(Minecraft.getMinecraft().mcDataDir, "webnei-exporter/reports");
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new IOException("Unable to create report directory: " + dir.getAbsolutePath());
        }
        File report = new File(dir, "handlers.tsv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(report))) {
            writer.write(
                "registration_index\tsource_list\tstable_handler_key\thandler_class\thandler_id\toverlay_id\trecipe_name\trecipe_tab_name\tresolved_category_id\tmod_id\tmod_name\ticon_stack_id\tcatalyst_key\tloaded_recipe_count\textraction_status\treason");
            writer.newLine();
            for (HandlerScanRow row : rows) {
                writer.write(Integer.toString(row.registrationIndex));
                writeCell(writer, row.sourceList);
                writeCell(writer, row.stableKey);
                writeCell(writer, row.handlerClass);
                writeCell(writer, row.handlerId);
                writeCell(writer, row.overlayId);
                writeCell(writer, row.recipeName);
                writeCell(writer, row.recipeTabName);
                writeCell(writer, row.resolvedCategoryId);
                writeCell(writer, row.modId);
                writeCell(writer, row.modName);
                writeCell(writer, row.iconStackId);
                writeCell(writer, row.catalystKey);
                writeCell(writer, Integer.toString(row.loadedRecipeCount));
                writeCell(writer, row.extractionStatus);
                writeCell(writer, row.reason);
                writer.newLine();
            }
        }
        return report;
    }

    private static void writeCell(BufferedWriter writer, String value) throws IOException {
        writer.write('\t');
        writer.write(
            value == null ? ""
                : value.replace('\t', ' ')
                    .replace('\r', ' ')
                    .replace('\n', ' '));
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
