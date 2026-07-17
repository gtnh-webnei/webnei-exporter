package moe.takochan.webnei.exporter.domain.item.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import moe.takochan.webnei.exporter.domain.item.model.ItemTooltipSnapshotRow;

class ItemVariantCollectorTest {

    @BeforeEach
    @AfterEach
    void clearOverrides() {
        TooltipKeyOverride.clear();
        TooltipBurnTimeOverride.clear();
    }

    @Test
    void fourStatesUseIndependentCanonicalCopiesAndShareOneBurnTimeSample() {
        ItemStack canonical = stack(new Item(), 5, 7, 11);
        List<ItemStack> tooltipStacks = new ArrayList<>();
        List<Integer> sharedBurnTimes = new ArrayList<>();
        int[] randomProducerCalls = { 0 };
        ItemVariantCollector.BurnTimeAccess burnTime = new ItemVariantCollector.BurnTimeAccess() {

            @Override
            public int sample(ItemStack sampleStack) {
                randomProducerCalls[0]++;
                assertNotSame(canonical, sampleStack);
                sampleStack.stackSize = 99;
                sampleStack.setItemDamage(98);
                sampleStack.getTagCompound()
                    .setInteger("value", 97);
                return 1000 + randomProducerCalls[0];
            }

            @Override
            public void activate(ItemStack canonicalStack, int sampledBurnTime) {
                TooltipBurnTimeOverride.activate(canonicalStack, sampledBurnTime);
            }

            @Override
            public void clear() {
                TooltipBurnTimeOverride.clear();
            }
        };
        ItemVariantCollector collector = new ItemVariantCollector(newKeySimulator(), burnTime, stack -> {
            tooltipStacks.add(stack);
            assertEquals(5, stack.stackSize);
            assertEquals(7, stack.getItemDamage());
            assertEquals(
                11,
                stack.getTagCompound()
                    .getInteger("value"));
            sharedBurnTimes.add(TooltipBurnTimeOverride.overrideFor(stack));
            stack.stackSize++;
            stack.setItemDamage(20 + tooltipStacks.size());
            stack.getTagCompound()
                .setInteger("value", 30 + tooltipStacks.size());
            return "tooltip-" + tooltipStacks.size();
        });

        List<ItemTooltipSnapshotRow> snapshots = collector.collectTooltipSnapshots("dataset", variant(), canonical);

        assertEquals(1, randomProducerCalls[0]);
        assertEquals(4, tooltipStacks.size());
        assertEquals(4, snapshots.size());
        Set<ItemStack> identities = new HashSet<>(tooltipStacks);
        assertEquals(4, identities.size());
        for (ItemStack tooltipStack : tooltipStacks) {
            assertNotSame(canonical, tooltipStack);
        }
        assertEquals(Integer.valueOf(1001), sharedBurnTimes.get(0));
        assertEquals(sharedBurnTimes.get(0), sharedBurnTimes.get(1));
        assertEquals(sharedBurnTimes.get(0), sharedBurnTimes.get(2));
        assertEquals(sharedBurnTimes.get(0), sharedBurnTimes.get(3));
        assertEquals(5, canonical.stackSize);
        assertEquals(7, canonical.getItemDamage());
        assertEquals(
            11,
            canonical.getTagCompound()
                .getInteger("value"));
        assertNull(TooltipBurnTimeOverride.overrideFor(canonical.copy()));
    }

    @Test
    void collectionFailureRemainsPrimaryWhenBurnClearAlsoFails() {
        RuntimeException actionFailure = new RuntimeException("action");
        ItemVariantCollector.BurnTimeAccess burnTime = new ItemVariantCollector.BurnTimeAccess() {

            @Override
            public int sample(ItemStack stack) {
                return 10;
            }

            @Override
            public void activate(ItemStack canonicalStack, int sampledBurnTime) {}

            @Override
            public void clear() {
                throw new RuntimeException("clear");
            }
        };
        ItemVariantCollector collector = new ItemVariantCollector(
            newKeySimulator(),
            burnTime,
            stack -> { throw actionFailure; });

        RuntimeException thrown = assertThrows(
            RuntimeException.class,
            () -> collector.collectTooltipSnapshots("dataset", variant(), stack(new Item(), 1, 0, 1)));

        assertSame(actionFailure, thrown);
        assertEquals(1, thrown.getSuppressed().length);
        assertEquals("Failed to end tooltip burn-time override", thrown.getSuppressed()[0].getMessage());
    }

    @Test
    void activationFailureDoesNotClearAnotherBurnScope() {
        int[] clearCalls = { 0 };
        ItemVariantCollector.BurnTimeAccess burnTime = new ItemVariantCollector.BurnTimeAccess() {

            @Override
            public int sample(ItemStack stack) {
                return 10;
            }

            @Override
            public void activate(ItemStack canonicalStack, int sampledBurnTime) {
                throw new RuntimeException("active scope");
            }

            @Override
            public void clear() {
                clearCalls[0]++;
            }
        };
        ItemVariantCollector collector = new ItemVariantCollector(newKeySimulator(), burnTime, stack -> "unused");

        assertThrows(
            IllegalStateException.class,
            () -> collector.collectTooltipSnapshots("dataset", variant(), stack(new Item(), 1, 0, 1)));

        assertEquals(0, clearCalls[0]);
    }

    @Test
    void linkageErrorsFromTooltipGenerationTerminateCollectionAndClearScopes() {
        Item item = new Item() {

            @Override
            public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
                throw new NoClassDefFoundError("missing tooltip dependency");
            }
        };
        ItemVariantCollector collector = new ItemVariantCollector(newKeySimulator());
        ItemStack canonical = stack(item, 1, 0, 1);

        assertThrows(
            NoClassDefFoundError.class,
            () -> collector.collectTooltipSnapshots("dataset", variant(), canonical));

        assertNull(TooltipKeyOverride.overrideFor(org.lwjgl.input.Keyboard.KEY_LSHIFT));
        assertNull(TooltipBurnTimeOverride.overrideFor(canonical.copy()));
    }

    private static TooltipKeySimulator newKeySimulator() {
        return new TooltipKeySimulator(new TooltipKeySimulator.Access() {

            @Override
            public boolean isPressed(int lwjglKey) {
                Boolean override = TooltipKeyOverride.overrideFor(lwjglKey);
                if (override == null) {
                    throw new AssertionError("Missing key override for " + lwjglKey);
                }
                return override.booleanValue();
            }

            @Override
            public void beginReleased() {
                TooltipKeyOverride.activateReleased();
            }

            @Override
            public void setPressed(int lwjglKey, boolean pressed) {
                TooltipKeyOverride.setPressed(lwjglKey, pressed);
            }

            @Override
            public void endOverride() {
                TooltipKeyOverride.clear();
            }
        });
    }

    private static ItemVariantIdentity variant() {
        return new ItemVariantIdentity("item:variant", "item:id", 7, "hash", "nbt");
    }

    private static ItemStack stack(Item item, int size, int damage, int nbtValue) {
        ItemStack stack = new ItemStack(item, size, damage);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("value", nbtValue);
        stack.setTagCompound(tag);
        return stack;
    }
}
