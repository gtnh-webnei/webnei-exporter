package moe.takochan.webnei.exporter.command;

import net.minecraft.command.ICommandSender;

/**
 * /webnei export 下的二级子命令契约。
 */
public interface ExportSubcommand {

    /**
     * 子命令名称，用于命令分发和补全。
     */
    String name();

    /**
     * 帮助列表中展示该子命令说明的本地化键。
     */
    String descriptionKey();

    /**
     * 执行子命令；参数数组不包含 /webnei export 和子命令自身。
     */
    void run(ICommandSender sender, String[] args);
}
