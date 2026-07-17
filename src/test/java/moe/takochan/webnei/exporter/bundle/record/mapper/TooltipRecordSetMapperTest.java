package moe.takochan.webnei.exporter.bundle.record.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import moe.takochan.webnei.exporter.bundle.record.BundleRecordSet;
import moe.takochan.webnei.exporter.domain.dataset.DatasetExportModel;
import moe.takochan.webnei.exporter.domain.dataset.model.DatasetRow;
import moe.takochan.webnei.exporter.domain.item.ItemExportModel;

class TooltipRecordSetMapperTest {

    @Test
    void datasetMapperOnlyEmitsDataset() {
        DatasetExportModel model = new DatasetExportModel(
            new DatasetRow("d", "p", "v", "variant", "en_US", "display", "5", "exporter", "now", "1.7.10"));

        List<BundleRecordSet> sets = new DatasetRecordSetMapper().recordSets(model);

        assertEquals(Collections.singletonList("dataset"), names(sets));
        assertEquals(Collections.singletonList(10), orders(sets));
    }

    @Test
    void snapshotFollowsVariantAndPrecedesToolClass() {
        ItemExportModel model = new ItemExportModel(
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList());

        List<BundleRecordSet> sets = new ItemRecordSetMapper().recordSets(model);

        assertEquals(
            Arrays.asList("item", "item_variant", "item_tooltip_snapshot", "item_tool_class", "item_list_entry"),
            names(sets));
        assertEquals(Arrays.asList(30, 40, 45, 50, 60), orders(sets));
        assertEquals(
            Arrays.asList("dataset_id", "item_variant_id", "tooltip_type", "key_state", "tooltip_text"),
            sets.get(2)
                .getFields());
        assertEquals(
            Arrays.asList(
                "dataset_id",
                "item_variant_id",
                "item_id",
                "damage",
                "nbt_hash",
                "nbt_text",
                "display_name",
                "chemical_expression"),
            sets.get(1)
                .getFields());
    }

    private static List<String> names(List<BundleRecordSet> sets) {
        java.util.ArrayList<String> names = new java.util.ArrayList<>();
        for (BundleRecordSet set : sets) {
            names.add(set.getName());
        }
        return names;
    }

    private static List<Integer> orders(List<BundleRecordSet> sets) {
        java.util.ArrayList<Integer> orders = new java.util.ArrayList<>();
        for (BundleRecordSet set : sets) {
            orders.add(set.getOrder());
        }
        return orders;
    }
}
