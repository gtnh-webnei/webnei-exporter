package moe.takochan.webnei.exporter.domain.item.internal;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/** Scoped burn-time result shared by tooltip snapshots of one canonical item stack. */
public final class TooltipBurnTimeOverride {

    private static boolean active;
    private static Item item;
    private static int damage;
    private static NBTTagCompound tagCompound;
    private static int burnTime;

    private TooltipBurnTimeOverride() {}

    public static synchronized void activate(ItemStack canonicalStack, int sampledBurnTime) {
        if (active) {
            throw new IllegalStateException("Tooltip burn-time override is already active");
        }
        item = canonicalStack.getItem();
        damage = canonicalStack.getItemDamage();
        NBTTagCompound canonicalTag = canonicalStack.getTagCompound();
        tagCompound = canonicalTag == null ? null : (NBTTagCompound) canonicalTag.copy();
        burnTime = sampledBurnTime;
        active = true;
    }

    public static synchronized Integer overrideFor(ItemStack stack) {
        if (!active || stack == null || stack.getItem() != item || stack.getItemDamage() != damage) {
            return null;
        }
        NBTTagCompound candidateTag = stack.getTagCompound();
        if (tagCompound == null ? candidateTag != null : !tagCompound.equals(candidateTag)) {
            return null;
        }
        return Integer.valueOf(burnTime);
    }

    public static synchronized void clear() {
        active = false;
        item = null;
        damage = 0;
        tagCompound = null;
        burnTime = 0;
    }
}
