package moe.takochan.webnei.exporter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class SchemaContractTest {

    @Test
    void baselineDefinesFinalTooltipSchemaWithoutMigrations() throws IOException {
        String sql = readSql(Paths.get("schema.sql"));
        String snapshot = statement(sql, "CREATE TABLE IF NOT EXISTS item_tooltip_snapshot");

        assertFalse(sql.contains("tooltip_profile"));
        assertFalse(sql.contains("profile_key"));
        assertFalse(sql.contains("recipe_candidate_annotation"));
        assertFalse(sql.contains("presentation_domain"));
        assertFalse(Files.exists(Paths.get("migrations")));

        assertTrue(snapshot.contains("tooltip_type TEXT NOT NULL CHECK (tooltip_type IN ('standard', 'key'))"));
        assertTrue(
            snapshot.contains(
                "key_state TEXT NOT NULL CHECK (key_state IN ('none', 'lshift', 'lcontrol', 'lshift_lcontrol'))"));
        assertTrue(snapshot.contains("PRIMARY KEY (dataset_id, item_variant_id, key_state)"));
        assertTrue(snapshot.contains("(tooltip_type = 'standard' AND key_state = 'none')"));
        assertTrue(snapshot.contains("(tooltip_type = 'key' AND key_state <> 'none')"));
        assertTrue(
            snapshot.contains(
                "FOREIGN KEY (dataset_id, item_variant_id) REFERENCES item_variant(dataset_id, item_variant_id) ON DELETE CASCADE"));
    }

    @Test
    void baselineDefinesFinalRecipeTooltipContract() throws IOException {
        String sql = readSql(Paths.get("schema.sql"));
        String layout = statement(sql, "CREATE TABLE IF NOT EXISTS recipe_slot_layout");
        String candidate = statement(sql, "CREATE TABLE IF NOT EXISTS recipe_slot_candidate");
        String fragment = statement(sql, "CREATE TABLE IF NOT EXISTS recipe_candidate_tooltip_fragment");
        String region = statement(sql, "CREATE TABLE IF NOT EXISTS recipe_tooltip_region");

        assertFalse(layout.contains("UNIQUE (dataset_id, category_id, role, x, y)"));
        assertTrue(layout.contains("PRIMARY KEY (dataset_id, category_id, slot_key)"));

        assertTrue(
            candidate.contains(
                "presentation_type TEXT NOT NULL CHECK (presentation_type IN ('itemStack', 'gtFluidDisplay', 'fluidSlot'))"));
        assertTrue(candidate.contains("presentation_id TEXT NOT NULL"));
        assertTrue(
            candidate.contains("amount_unit TEXT NOT NULL CHECK (amount_unit IN ('item', 'L', 'mB', 'mBPerTick'))"));
        assertTrue(candidate.contains("probability >= 0 AND probability <= 1"));

        assertTrue(fragment.contains("fragment_order INTEGER NOT NULL CHECK (fragment_order >= 0)"));
        assertTrue(
            fragment.contains(
                "state_key TEXT NOT NULL CHECK (state_key IN ('all', 'none', 'lshift', 'lcontrol', 'lshift_lcontrol'))"));
        assertTrue(fragment.contains("text_value TEXT NOT NULL"));
        assertTrue(fragment.contains("PRIMARY KEY (dataset_id, recipe_id, slot_key, candidate_order, fragment_order)"));
        assertTrue(
            fragment.contains(
                "FOREIGN KEY (dataset_id, recipe_id, slot_key, candidate_order) REFERENCES recipe_slot_candidate(dataset_id, recipe_id, slot_key, candidate_order) ON DELETE CASCADE"));

        assertTrue(region.contains("region_order INTEGER NOT NULL CHECK (region_order >= 0)"));
        assertTrue(region.contains("region_type TEXT NOT NULL CHECK (region_type IN ('fluidTank', 'recipeTitle'))"));
        assertTrue(region.contains("x INTEGER NOT NULL"));
        assertTrue(region.contains("y INTEGER NOT NULL"));
        assertTrue(region.contains("width INTEGER NOT NULL CHECK (width > 0)"));
        assertTrue(region.contains("height INTEGER NOT NULL CHECK (height > 0)"));
        assertTrue(
            region.contains(
                "state_key TEXT NOT NULL CHECK (state_key IN ('all', 'none', 'lshift', 'lcontrol', 'lshift_lcontrol'))"));
        assertTrue(region.contains("tooltip_text TEXT NOT NULL"));
        assertTrue(region.contains("PRIMARY KEY (dataset_id, recipe_id, region_order, state_key)"));
        assertTrue(
            region.contains(
                "FOREIGN KEY (dataset_id, recipe_id) REFERENCES recipe(dataset_id, recipe_id) ON DELETE CASCADE"));
    }

    @Test
    void browserAndSearchViewsUseApprovedSnapshotContract() throws IOException {
        String sql = readSql(Paths.get("schema.sql"));
        String itemBrowser = statement(sql, "CREATE OR REPLACE VIEW v_item_browser");
        String fluidBrowser = statement(sql, "CREATE OR REPLACE VIEW v_fluid_browser");
        String itemSearch = statement(sql, "CREATE MATERIALIZED VIEW mv_item_search");

        assertSelectedColumns(
            itemBrowser,
            "ile.dataset_id",
            "iv.item_variant_id",
            "i.mod_id",
            "m.name AS mod_name",
            "i.registry_name",
            "iv.display_name",
            "ile.list_index",
            "a.path AS icon_path",
            "a.width AS icon_width",
            "a.height AS icon_height",
            "a.metadata_json AS icon_metadata_json");
        assertDoesNotSelect(
            itemBrowser,
            "iv.item_id",
            "i.unlocalized_name",
            "iv.damage",
            "iv.nbt_hash",
            "iv.chemical_expression",
            "a.media_type AS icon_media_type");
        assertFalse(itemBrowser.contains("item_tooltip_snapshot"));
        assertFalse(itemBrowser.contains("tooltip_text"));

        assertSelectedColumns(
            fluidBrowser,
            "f.dataset_id",
            "f.fluid_id",
            "f.mod_id",
            "m.name AS mod_name",
            "f.registry_name",
            "f.display_name",
            "a.path AS icon_path",
            "a.width AS icon_width",
            "a.height AS icon_height",
            "a.metadata_json AS icon_metadata_json");
        assertDoesNotSelect(
            fluidBrowser,
            "f.unlocalized_name",
            "f.chemical_expression",
            "f.luminosity",
            "f.density",
            "f.temperature",
            "f.viscosity",
            "f.gaseous",
            "a.media_type AS icon_media_type");
        assertFalse(fluidBrowser.contains("item_tooltip_snapshot"));
        assertFalse(fluidBrowser.contains("tooltip_text"));

        assertTrue(itemSearch.contains("JOIN item_tooltip_snapshot its"));
        assertTrue(itemSearch.contains("its.tooltip_type = 'standard'"));
        assertTrue(itemSearch.contains("its.key_state = 'none'"));
        assertTrue(itemSearch.contains("b.tooltip_norm AS s_tooltip"));
        assertFalse(itemSearch.contains("profile_key"));
    }

    private static String readSql(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8).replaceAll("\\s+", " ");
    }

    private static String statement(String sql, String prefix) {
        int start = sql.indexOf(prefix);
        assertTrue(start >= 0, prefix);
        int end = sql.indexOf(';', start);
        assertTrue(end >= 0, prefix);
        return sql.substring(start, end);
    }

    private static String selectList(String statement) {
        int start = statement.indexOf("SELECT ");
        assertTrue(start >= 0, statement);
        int end = statement.indexOf(" FROM ", start);
        assertTrue(end >= 0, statement);
        return statement.substring(start + "SELECT ".length(), end);
    }

    private static void assertSelectedColumns(String statement, String... columns) {
        assertTrue(selectList(statement).equals(String.join(", ", columns)), statement);
    }

    private static void assertDoesNotSelect(String statement, String... columns) {
        String selectList = ", " + selectList(statement) + ",";
        for (String column : columns) {
            assertFalse(selectList.contains(", " + column + ","), column);
        }
    }
}
