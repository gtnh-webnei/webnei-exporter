package moe.takochan.webnei.exporter.command;

import net.minecraft.command.ICommandSender;

interface ExportSubcommand {

    String name();

    void run(ICommandSender sender);
}
