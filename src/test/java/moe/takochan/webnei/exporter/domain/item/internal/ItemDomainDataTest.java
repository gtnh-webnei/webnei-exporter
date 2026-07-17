package moe.takochan.webnei.exporter.domain.item.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.junit.jupiter.api.Test;

import moe.takochan.webnei.exporter.domain.item.ItemExportModel;
import moe.takochan.webnei.exporter.domain.item.model.ItemTooltipSnapshotRow;
import moe.takochan.webnei.exporter.domain.item.model.ItemVariantRow;

class ItemDomainDataTest {

    @Test
    void variantRegistrationStoresAllDataAndModelConversionDoesNotFreeze() {
        ItemDomainData data = new ItemDomainData();
        ItemStack firstStack = new ItemStack((Item) null, 0, 0);
        ItemVariantRow firstVariant = variant("variant:first", "item:first");
        List<ItemTooltipSnapshotRow> firstSnapshots = snapshots("variant:first", "First");

        data.putVariant(firstVariant, firstStack, firstSnapshots);
        ItemExportModel firstModel = (ItemExportModel) data.toExportModel();

        assertEquals(
            1,
            firstModel.getVariants()
                .size());
        assertSame(
            firstVariant,
            firstModel.getVariants()
                .get(0));
        assertEquals(
            4,
            firstModel.getTooltipSnapshots()
                .size());
        for (int index = 0; index < firstSnapshots.size(); index++) {
            assertSame(
                firstSnapshots.get(index),
                firstModel.getTooltipSnapshots()
                    .get(index));
        }
        assertTrue(
            data.stacks()
                .containsKey("variant:first"));
        assertSame(
            firstStack,
            data.stacks()
                .get("variant:first"));

        ItemStack secondStack = new ItemStack((Item) null, 0, 0);
        data.putVariant(variant("variant:second", "item:second"), secondStack, snapshots("variant:second", "Second"));
        ItemExportModel secondModel = (ItemExportModel) data.toExportModel();

        assertEquals(
            1,
            firstModel.getVariants()
                .size());
        assertEquals(
            4,
            firstModel.getTooltipSnapshots()
                .size());
        assertEquals(
            2,
            secondModel.getVariants()
                .size());
        assertEquals(
            8,
            secondModel.getTooltipSnapshots()
                .size());
        assertEquals(
            2,
            data.stacks()
                .size());
        assertSame(
            secondStack,
            data.stacks()
                .get("variant:second"));
    }

    private static ItemVariantRow variant(String itemVariantId, String itemId) {
        return new ItemVariantRow("dataset", itemVariantId, itemId, 0, "", "", itemVariantId);
    }

    private static List<ItemTooltipSnapshotRow> snapshots(String itemVariantId, String tooltipText) {
        return Arrays.asList(
            snapshot(itemVariantId, TooltipKeyState.NONE, tooltipText),
            snapshot(itemVariantId, TooltipKeyState.LSHIFT, tooltipText),
            snapshot(itemVariantId, TooltipKeyState.LCONTROL, tooltipText),
            snapshot(itemVariantId, TooltipKeyState.LSHIFT_LCONTROL, tooltipText));
    }

    private static ItemTooltipSnapshotRow snapshot(String itemVariantId, TooltipKeyState state, String tooltipText) {
        return new ItemTooltipSnapshotRow(
            "dataset",
            itemVariantId,
            state.tooltipType(),
            state.persistedKey(),
            tooltipText);
    }
}
