package moe.takochan.webnei.exporter.command;

import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

import org.jetbrains.annotations.NotNull;

/**
 * WebNEI 导出器的游戏命令入口。
 */
public final class WebneiCommand implements ICommand {

    /**
     * 负责解析 /webnei 子命令并转发到具体导出命令。
     */
    private final WebneiCommandRouter router = new WebneiCommandRouter();

    /**
     * 返回游戏内注册的根命令名称。
     */
    @Override
    public String getCommandName() {
        return "webnei";
    }

    /**
     * 返回根命令用法的本地化键。
     */
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "webnei.command.usage";
    }

    /**
     * 当前不提供命令别名。
     */
    @Override
    public List<String> getCommandAliases() {
        return null;
    }

    /**
     * 执行命令；路由器无法识别参数时发送总用法。
     */
    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!router.run(sender, args)) {
            router.sendUsage(sender);
        }
    }

    /**
     * 允许任意命令发送者使用该命令。
     */
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    /**
     * 返回当前参数位置的补全候选。
     */
    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return router.complete(args);
    }

    /**
     * 本命令不把任何参数位置视为玩家名。
     */
    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    /**
     * 按根命令名称参与游戏命令排序。
     */
    @Override
    public int compareTo(@NotNull Object other) {
        if (other instanceof ICommand command) {
            return getCommandName().compareTo(command.getCommandName());
        }
        return 0;
    }
}
