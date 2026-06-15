package moe.takochan.webnei.exporter.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;

/**
 * 命令层聊天消息发送工具，统一使用游戏本地化键。
 */
public final class CommandMessageSender {

    private CommandMessageSender() {
    }

    /**
     * 向命令发送者发送一条可本地化聊天消息。
     */
    public static void send(ICommandSender sender, String key, Object... args) {
        sender.addChatMessage(new ChatComponentTranslation(key, args));
    }
}
