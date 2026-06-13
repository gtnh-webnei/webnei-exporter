package moe.takochan.webnei.exporter.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;

final class CommandMessages {

    private CommandMessages() {}

    static void send(ICommandSender sender, String key, Object... args) {
        sender.addChatMessage(new ChatComponentTranslation(key, args));
    }
}
