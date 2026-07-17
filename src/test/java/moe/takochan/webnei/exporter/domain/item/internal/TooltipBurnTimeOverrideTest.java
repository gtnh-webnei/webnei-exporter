package moe.takochan.webnei.exporter.domain.item.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TooltipBurnTimeOverrideTest {

    @BeforeEach
    @AfterEach
    void clearOverride() {
        TooltipBurnTimeOverride.clear();
    }

    @Test
    void matchingCopyUsesCachedValueAndIgnoresStackSize() {
        ItemStack canonical = stack(new Item(), 3, 7, 11);
        TooltipBurnTimeOverride.activate(canonical, 2400);
        ItemStack copy = canonical.copy();
        copy.stackSize = 64;

        assertEquals(Integer.valueOf(2400), TooltipBurnTimeOverride.overrideFor(copy));
    }

    @Test
    void itemDamageAndNbtMustAllMatch() {
        Item item = new Item();
        ItemStack canonical = stack(item, 1, 7, 11);
        TooltipBurnTimeOverride.activate(canonical, 80);

        assertNull(TooltipBurnTimeOverride.overrideFor(stack(new Item(), 1, 7, 11)));
        assertNull(TooltipBurnTimeOverride.overrideFor(stack(item, 1, 8, 11)));
        assertNull(TooltipBurnTimeOverride.overrideFor(stack(item, 1, 7, 12)));
    }

    @Test
    void zeroIsARealCachedValue() {
        ItemStack canonical = stack(new Item(), 1, 0, 3);
        TooltipBurnTimeOverride.activate(canonical, 0);

        assertEquals(Integer.valueOf(0), TooltipBurnTimeOverride.overrideFor(canonical.copy()));
    }

    @Test
    void activationDeepCopiesCanonicalNbt() {
        ItemStack canonical = stack(new Item(), 1, 0, 3);
        TooltipBurnTimeOverride.activate(canonical, 50);
        canonical.getTagCompound()
            .setInteger("value", 4);

        assertEquals(Integer.valueOf(50), TooltipBurnTimeOverride.overrideFor(stack(canonical.getItem(), 2, 0, 3)));
        assertNull(TooltipBurnTimeOverride.overrideFor(canonical));
    }

    @Test
    void nestedActivationDoesNotReplaceActiveScope() {
        ItemStack first = stack(new Item(), 1, 0, 1);
        TooltipBurnTimeOverride.activate(first, 10);

        assertThrows(
            IllegalStateException.class,
            () -> TooltipBurnTimeOverride.activate(stack(new Item(), 1, 0, 2), 20));
        assertEquals(Integer.valueOf(10), TooltipBurnTimeOverride.overrideFor(first.copy()));
    }

    @Test
    void clearRemovesTheScope() {
        ItemStack canonical = stack(new Item(), 1, 0, 1);
        TooltipBurnTimeOverride.activate(canonical, 10);

        TooltipBurnTimeOverride.clear();

        assertNull(TooltipBurnTimeOverride.overrideFor(canonical.copy()));
    }

    private static ItemStack stack(Item item, int size, int damage, int nbtValue) {
        ItemStack stack = new ItemStack(item, size, damage);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("value", nbtValue);
        stack.setTagCompound(tag);
        return stack;
    }
}
