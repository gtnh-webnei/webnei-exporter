package moe.takochan.webnei.exporter.command;

import net.minecraft.command.ICommandSender;

interface ExportSubcommand {

    String name();

    String descriptionKey();

    void run(ICommandSender sender, String[] args);
}
