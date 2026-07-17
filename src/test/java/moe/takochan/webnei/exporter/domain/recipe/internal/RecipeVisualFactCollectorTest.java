package moe.takochan.webnei.exporter.domain.recipe.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.junit.jupiter.api.Test;

import codechicken.nei.PositionedStack;

class RecipeVisualFactCollectorTest {

    @Test
    void activatesEachValidCandidateInSourceOrderAndRestoresOriginalBeforeTheNextActivation() {
        ItemStack original = stack(7);
        TrackingPositionedStack positioned = new TrackingPositionedStack(new ItemStack[] { stack(2), stack(5) });
        Item firstItem = positioned.items[0].getItem();
        Item secondItem = positioned.items[1].getItem();
        positioned.item = original;
        List<Integer> sourceIndices = new ArrayList<>();
        List<ItemStack> active = new ArrayList<>();

        RecipeVisualFactCollector.forEachActiveCandidate(positioned, (sourceIndex, candidate) -> {
            sourceIndices.add(sourceIndex);
            active.add(candidate);
        });

        assertEquals(2, active.size());
        assertEquals(0, sourceIndices.get(0));
        assertEquals(1, sourceIndices.get(1));
        assertSame(
            firstItem,
            active.get(0)
                .getItem());
        assertEquals(2, active.get(0).stackSize);
        assertSame(
            secondItem,
            active.get(1)
                .getItem());
        assertEquals(5, active.get(1).stackSize);
        assertEquals(2, positioned.itemsBeforeActivation.size());
        assertSame(original, positioned.itemsBeforeActivation.get(0));
        assertSame(original, positioned.itemsBeforeActivation.get(1));
        assertSame(original, positioned.item);
    }

    @Test
    void reportsOriginalFallbackWithNegativeSourceIndex() {
        ItemStack original = stack(7);
        TrackingPositionedStack positioned = new TrackingPositionedStack(new ItemStack[] { original });
        positioned.items = new ItemStack[0];
        positioned.item = original;
        List<Integer> sourceIndices = new ArrayList<>();

        RecipeVisualFactCollector
            .forEachActiveCandidate(positioned, (sourceIndex, active) -> sourceIndices.add(sourceIndex));

        assertEquals(1, sourceIndices.size());
        assertEquals(-1, sourceIndices.get(0));
        assertSame(original, positioned.item);
    }

    @Test
    void restoresOriginalBetweenCandidatesWhenLaterCandidateProcessingFails() {
        ItemStack original = stack(7);
        TrackingPositionedStack positioned = new TrackingPositionedStack(new ItemStack[] { stack(2), stack(5) });
        positioned.item = original;
        int[] calls = { 0 };

        assertThrows(
            IllegalStateException.class,
            () -> RecipeVisualFactCollector.forEachActiveCandidate(positioned, (sourceIndex, active) -> {
                assertEquals(calls[0], sourceIndex);
                calls[0]++;
                if (calls[0] == 2) {
                    throw new IllegalStateException("metadata failure");
                }
            }));

        assertEquals(2, calls[0]);
        assertEquals(2, positioned.itemsBeforeActivation.size());
        assertSame(original, positioned.itemsBeforeActivation.get(0));
        assertSame(original, positioned.itemsBeforeActivation.get(1));
        assertSame(original, positioned.item);
    }

    private static ItemStack stack(int amount) {
        return new ItemStack(new Item(), amount);
    }

    private static final class TrackingPositionedStack extends PositionedStack {

        private final List<ItemStack> itemsBeforeActivation = new ArrayList<>();

        private TrackingPositionedStack(ItemStack[] items) {
            super(items, 1, 2);
        }

        @Override
        public void setPermutationToRender(int index) {
            if (itemsBeforeActivation != null) {
                itemsBeforeActivation.add(item);
            }
            super.setPermutationToRender(index);
        }
    }
}
