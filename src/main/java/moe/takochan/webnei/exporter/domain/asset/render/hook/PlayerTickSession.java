package moe.takochan.webnei.exporter.domain.asset.render.hook;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

/**
 * 通过临时改写 {@code mc.thePlayer.ticksExisted} 推进时间的会话。
 * 适用于所有读 {@link net.minecraft.entity.Entity#ticksExisted} 的客户端渲染器（cosmic shader、
 * ThermalArmor、CastingBracelet、Katana、PrimordialGauntlet、CellMicroscope、Thaumometer 等）。
 *
 * <p>
 * 不依赖反射——{@code Entity#ticksExisted} 是 public 字段。{@link #close} 时复原原值。
 */
public final class PlayerTickSession implements ITimeDriverSession {

    private final EntityPlayer player;
    private final int baseline;

    private PlayerTickSession(EntityPlayer player) {
        this.player = player;
        this.baseline = player.ticksExisted;
    }

    /** 客户端玩家未就绪时返回 null，调用方应据此退化为单帧静态。 */
    public static PlayerTickSession beginIfReady() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        return player == null ? null : new PlayerTickSession(player);
    }

    @Override
    public void advanceTo(int step) {
        player.ticksExisted = baseline + step;
    }

    @Override
    public void close() {
        player.ticksExisted = baseline;
    }
}
