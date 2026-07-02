-- NESQL Exporter PostgreSQL schema v3
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE IF NOT EXISTS dataset (
  dataset_id TEXT PRIMARY KEY,
  pack_slug TEXT NOT NULL,
  pack_version TEXT NOT NULL,
  variant TEXT NOT NULL,
  language TEXT NOT NULL,
  display_name TEXT NOT NULL,
  schema_version TEXT NOT NULL,
  exporter_version TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  minecraft_version TEXT NOT NULL,
  UNIQUE (pack_slug, pack_version, variant, language)
);

CREATE TABLE IF NOT EXISTS mod (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  mod_id TEXT NOT NULL,
  name TEXT NOT NULL,
  version TEXT NOT NULL,
  source_type TEXT NOT NULL,
  source_file_name TEXT NOT NULL,
  source_sha256 TEXT NOT NULL,
  enabled BOOLEAN NOT NULL,
  PRIMARY KEY (dataset_id, mod_id)
);

CREATE TABLE IF NOT EXISTS asset (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  owner_type TEXT NOT NULL,
  owner_id TEXT NOT NULL,
  kind TEXT NOT NULL,
  path TEXT NOT NULL,
  media_type TEXT NOT NULL,
  width INTEGER NOT NULL,
  height INTEGER NOT NULL,
  metadata_json TEXT NOT NULL,
  PRIMARY KEY (dataset_id, owner_type, owner_id, kind)
);

CREATE TABLE IF NOT EXISTS item (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  item_id TEXT NOT NULL,
  mod_id TEXT NOT NULL,
  registry_name TEXT NOT NULL,
  unlocalized_name TEXT NOT NULL,
  search_text TEXT GENERATED ALWAYS AS (
    lower(item_id)
  ) STORED,
  max_stack_size INTEGER NOT NULL,
  max_damage INTEGER NOT NULL,
  runtime_item_id INTEGER NOT NULL,
  PRIMARY KEY (dataset_id, item_id)
);

CREATE TABLE IF NOT EXISTS item_variant (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  item_variant_id TEXT NOT NULL,
  item_id TEXT NOT NULL,
  damage INTEGER NOT NULL,
  nbt_hash TEXT NOT NULL,
  nbt_text TEXT NOT NULL,
  display_name TEXT NOT NULL,
  tooltip_text TEXT NOT NULL,
  chemical_expression TEXT NOT NULL DEFAULT '',
  search_text TEXT GENERATED ALWAYS AS (
    lower(item_variant_id || ' ' || item_id || ' ' || display_name || ' ' || regexp_replace(tooltip_text, '§.', '', 'g'))
  ) STORED,
  PRIMARY KEY (dataset_id, item_variant_id)
);

CREATE TABLE IF NOT EXISTS item_tool_class (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  item_variant_id TEXT NOT NULL,
  tool_class TEXT NOT NULL,
  harvest_level INTEGER NOT NULL,
  PRIMARY KEY (dataset_id, item_variant_id, tool_class)
);

CREATE TABLE IF NOT EXISTS item_list_entry (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  item_variant_id TEXT NOT NULL,
  list_index INTEGER NOT NULL,
  PRIMARY KEY (dataset_id, item_variant_id)
);

CREATE TABLE IF NOT EXISTS fluid (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  fluid_id TEXT NOT NULL,
  mod_id TEXT NOT NULL,
  registry_name TEXT NOT NULL,
  unlocalized_name TEXT NOT NULL,
  display_name TEXT NOT NULL,
  chemical_expression TEXT NOT NULL DEFAULT '',
  search_text TEXT GENERATED ALWAYS AS (
    lower(fluid_id || ' ' || registry_name || ' ' || display_name || ' ' || chemical_expression)
  ) STORED,
  luminosity INTEGER NOT NULL,
  density INTEGER NOT NULL,
  temperature INTEGER NOT NULL,
  viscosity INTEGER NOT NULL,
  gaseous BOOLEAN NOT NULL,
  PRIMARY KEY (dataset_id, fluid_id)
);

CREATE TABLE IF NOT EXISTS mob (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  mob_id TEXT NOT NULL,
  mod_id TEXT NOT NULL,
  entity_name TEXT NOT NULL,
  display_name TEXT NOT NULL,
  width DOUBLE PRECISION NOT NULL,
  height DOUBLE PRECISION NOT NULL,
  max_health DOUBLE PRECISION NOT NULL,
  armor INTEGER NOT NULL,
  immune_to_fire BOOLEAN NOT NULL,
  leashable BOOLEAN NOT NULL,
  PRIMARY KEY (dataset_id, mob_id)
);

CREATE TABLE IF NOT EXISTS mob_variant (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  mob_variant_id TEXT NOT NULL,
  mob_id TEXT NOT NULL,
  nbt_hash TEXT NOT NULL,
  nbt_text TEXT NOT NULL,
  PRIMARY KEY (dataset_id, mob_variant_id)
);

CREATE TABLE IF NOT EXISTS recipe_category (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  category_id TEXT NOT NULL,
  display_name TEXT NOT NULL,
  mod_id TEXT NOT NULL,
  canvas_width INTEGER NOT NULL,
  canvas_height INTEGER NOT NULL,
  PRIMARY KEY (dataset_id, category_id)
);

CREATE TABLE IF NOT EXISTS recipe_category_catalyst (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  category_id TEXT NOT NULL,
  item_variant_id TEXT NOT NULL,
  display_order INTEGER NOT NULL,
  PRIMARY KEY (dataset_id, category_id, item_variant_id),
  FOREIGN KEY (dataset_id, category_id)
    REFERENCES recipe_category(dataset_id, category_id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS recipe_slot_layout (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  category_id TEXT NOT NULL,
  slot_key TEXT NOT NULL,
  role TEXT NOT NULL,
  x INTEGER NOT NULL,
  y INTEGER NOT NULL,
  width INTEGER NOT NULL,
  height INTEGER NOT NULL,
  display_order INTEGER NOT NULL,
  PRIMARY KEY (dataset_id, category_id, slot_key),
  UNIQUE (dataset_id, category_id, role, x, y)
);

CREATE TABLE IF NOT EXISTS recipe (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  recipe_id TEXT NOT NULL,
  category_id TEXT NOT NULL,
  display_order INTEGER NOT NULL,
  PRIMARY KEY (dataset_id, recipe_id)
);

CREATE TABLE IF NOT EXISTS recipe_slot_candidate (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  recipe_id TEXT NOT NULL,
  slot_key TEXT NOT NULL,
  candidate_order INTEGER NOT NULL,
  target_domain TEXT NOT NULL,
  target_id TEXT NOT NULL,
  amount INTEGER NOT NULL,
  probability DOUBLE PRECISION NOT NULL DEFAULT 1,
  PRIMARY KEY (dataset_id, recipe_id, slot_key, candidate_order)
);

CREATE TABLE IF NOT EXISTS ore_dictionary (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  dictionary_name TEXT NOT NULL,
  PRIMARY KEY (dataset_id, dictionary_name)
);

CREATE TABLE IF NOT EXISTS ore_dictionary_entry (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  dictionary_name TEXT NOT NULL,
  item_variant_id TEXT NOT NULL,
  list_index INTEGER NOT NULL,
  PRIMARY KEY (dataset_id, dictionary_name, item_variant_id),
  FOREIGN KEY (dataset_id, dictionary_name)
    REFERENCES ore_dictionary(dataset_id, dictionary_name)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS fluid_container (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  fluid_id TEXT NOT NULL,
  amount INTEGER NOT NULL,
  item_variant_id TEXT NOT NULL,
  PRIMARY KEY (dataset_id, fluid_id, item_variant_id)
);

CREATE TABLE IF NOT EXISTS fluid_block (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  fluid_id TEXT NOT NULL,
  item_variant_id TEXT NOT NULL,
  PRIMARY KEY (dataset_id, fluid_id, item_variant_id)
);

-- Generic recipe metadata (EAV). Any mod's extra recipe data lives here as
-- key-value pairs; the display spec decides how each key renders. Replaces the
-- former gregtech_recipe + gregtech_recipe_metadata tables.
CREATE TABLE IF NOT EXISTS recipe_metadata (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  recipe_id TEXT NOT NULL,
  metadata_key TEXT NOT NULL,
  value_type TEXT NOT NULL,
  value_text TEXT,
  value_json JSONB,
  PRIMARY KEY (dataset_id, recipe_id, metadata_key)
);

-- Generic filterable recipe tags. Dimensions that need to be queried/sorted in
-- SQL (e.g. GregTech voltage_tier) are written here. tag_value is the display
-- string; sort_value gives a numeric ordering key (e.g. voltage in EU).
CREATE TABLE IF NOT EXISTS recipe_filter_tag (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  recipe_id TEXT NOT NULL,
  tag_key TEXT NOT NULL,
  tag_value TEXT NOT NULL,
  sort_value DOUBLE PRECISION,
  PRIMARY KEY (dataset_id, recipe_id, tag_key)
);

CREATE TABLE IF NOT EXISTS mob_info (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  mob_variant_id TEXT NOT NULL,
  allowed_in_peaceful BOOLEAN NOT NULL,
  soul_vial_usable BOOLEAN NOT NULL,
  allowed_infernal BOOLEAN NOT NULL,
  always_infernal BOOLEAN NOT NULL,
  PRIMARY KEY (dataset_id, mob_variant_id)
);

CREATE TABLE IF NOT EXISTS mob_drop (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  mob_variant_id TEXT NOT NULL,
  list_index INTEGER NOT NULL,
  drop_type TEXT NOT NULL,
  item_variant_id TEXT NOT NULL,
  stack_size INTEGER NOT NULL,
  probability DOUBLE PRECISION NOT NULL,
  lootable BOOLEAN NOT NULL,
  player_only BOOLEAN NOT NULL,
  PRIMARY KEY (dataset_id, mob_variant_id, list_index)
);

CREATE TABLE IF NOT EXISTS mob_spawn_info (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  mob_variant_id TEXT NOT NULL,
  list_index INTEGER NOT NULL,
  spawn_info TEXT NOT NULL,
  PRIMARY KEY (dataset_id, mob_variant_id, list_index)
);

CREATE TABLE IF NOT EXISTS quest_line (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  quest_line_id TEXT NOT NULL,
  icon_item_variant_id TEXT NOT NULL,
  name TEXT NOT NULL,
  description TEXT NOT NULL,
  visibility TEXT NOT NULL,
  order_index INTEGER NOT NULL,
  PRIMARY KEY (dataset_id, quest_line_id)
);

CREATE TABLE IF NOT EXISTS quest (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  quest_id TEXT NOT NULL,
  icon_item_variant_id TEXT NOT NULL,
  name TEXT NOT NULL,
  description TEXT NOT NULL,
  visibility TEXT NOT NULL,
  repeat_time INTEGER NOT NULL,
  quest_logic TEXT NOT NULL,
  task_logic TEXT NOT NULL,
  PRIMARY KEY (dataset_id, quest_id)
);

CREATE TABLE IF NOT EXISTS quest_dependency (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  quest_id TEXT NOT NULL,
  required_quest_id TEXT NOT NULL,
  PRIMARY KEY (dataset_id, quest_id, required_quest_id)
);

CREATE TABLE IF NOT EXISTS quest_line_entry (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  quest_line_id TEXT NOT NULL,
  quest_id TEXT NOT NULL,
  pos_x INTEGER NOT NULL,
  pos_y INTEGER NOT NULL,
  size_x INTEGER NOT NULL,
  size_y INTEGER NOT NULL,
  PRIMARY KEY (dataset_id, quest_line_id, quest_id)
);

CREATE TABLE IF NOT EXISTS quest_task (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  task_id TEXT NOT NULL,
  quest_id TEXT NOT NULL,
  list_index INTEGER NOT NULL,
  name TEXT NOT NULL,
  task_type TEXT NOT NULL,
  consume BOOLEAN NOT NULL,
  mob_variant_id TEXT NOT NULL,
  number_required INTEGER NOT NULL,
  dimension_name TEXT NOT NULL,
  PRIMARY KEY (dataset_id, task_id)
);

CREATE TABLE IF NOT EXISTS quest_task_item (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  task_id TEXT NOT NULL,
  list_index INTEGER NOT NULL,
  group_id TEXT NOT NULL,
  PRIMARY KEY (dataset_id, task_id, list_index)
);

CREATE TABLE IF NOT EXISTS quest_task_fluid (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  task_id TEXT NOT NULL,
  list_index INTEGER NOT NULL,
  fluid_id TEXT NOT NULL,
  amount INTEGER NOT NULL,
  PRIMARY KEY (dataset_id, task_id, list_index)
);

CREATE TABLE IF NOT EXISTS quest_reward (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  reward_id TEXT NOT NULL,
  quest_id TEXT NOT NULL,
  list_index INTEGER NOT NULL,
  name TEXT NOT NULL,
  reward_type TEXT NOT NULL,
  command TEXT NOT NULL,
  xp INTEGER NOT NULL,
  levels BOOLEAN NOT NULL,
  complete_quest_id TEXT NOT NULL,
  PRIMARY KEY (dataset_id, reward_id)
);

CREATE TABLE IF NOT EXISTS quest_reward_item (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  reward_id TEXT NOT NULL,
  list_index INTEGER NOT NULL,
  group_id TEXT NOT NULL,
  PRIMARY KEY (dataset_id, reward_id, list_index)
);

CREATE TABLE IF NOT EXISTS aspect (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  aspect_id TEXT NOT NULL,
  icon_item_variant_id TEXT NOT NULL,
  name TEXT NOT NULL,
  description TEXT NOT NULL,
  primal BOOLEAN NOT NULL,
  PRIMARY KEY (dataset_id, aspect_id)
);

CREATE TABLE IF NOT EXISTS aspect_component (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  aspect_id TEXT NOT NULL,
  component_aspect_id TEXT NOT NULL,
  PRIMARY KEY (dataset_id, aspect_id, component_aspect_id)
);

CREATE TABLE IF NOT EXISTS aspect_item (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  aspect_id TEXT NOT NULL,
  item_variant_id TEXT NOT NULL,
  amount INTEGER NOT NULL,
  PRIMARY KEY (dataset_id, aspect_id, item_variant_id)
);

-- Query indexes for frontend browsing and NEI-style lookups.
CREATE INDEX IF NOT EXISTS idx_asset_owner
  ON asset (dataset_id, owner_id, owner_type);
CREATE INDEX IF NOT EXISTS idx_asset_kind
  ON asset (dataset_id, kind, owner_type);
CREATE INDEX IF NOT EXISTS idx_nesql_item_mod_registry
  ON item (dataset_id, mod_id, registry_name);
CREATE INDEX IF NOT EXISTS idx_nesql_item_variant_item_damage
  ON item_variant (dataset_id, item_id, damage);
CREATE INDEX IF NOT EXISTS idx_nesql_item_variant_display_name
  ON item_variant (dataset_id, display_name);
CREATE INDEX IF NOT EXISTS idx_nesql_item_search_trgm
  ON item USING gin (search_text gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_nesql_item_variant_search_trgm
  ON item_variant USING gin (search_text gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_item_list_entry_order
  ON item_list_entry (dataset_id, list_index);
CREATE INDEX IF NOT EXISTS idx_nesql_fluid_mod_display
  ON fluid (dataset_id, mod_id, display_name, fluid_id);
CREATE INDEX IF NOT EXISTS idx_nesql_fluid_search_trgm
  ON fluid USING gin (search_text gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_nesql_mob_mod_entity
  ON mob (dataset_id, mod_id, entity_name);
CREATE INDEX IF NOT EXISTS idx_nesql_mob_variant_mob
  ON mob_variant (dataset_id, mob_id);
CREATE INDEX IF NOT EXISTS idx_nesql_recipe_category_catalyst_item
  ON recipe_category_catalyst (dataset_id, item_variant_id, category_id);
CREATE INDEX IF NOT EXISTS idx_nesql_recipe_category_catalyst_order
  ON recipe_category_catalyst (dataset_id, category_id, display_order, item_variant_id);
CREATE INDEX IF NOT EXISTS idx_nesql_recipe_by_category_order
  ON recipe (dataset_id, category_id, display_order, recipe_id);
CREATE INDEX IF NOT EXISTS idx_recipe_slot_layout_category_order
  ON recipe_slot_layout (dataset_id, category_id, display_order, slot_key);
CREATE INDEX IF NOT EXISTS idx_recipe_slot_layout_category_role_order
  ON recipe_slot_layout (dataset_id, category_id, role, display_order, slot_key);
CREATE INDEX IF NOT EXISTS idx_recipe_slot_candidate_target
  ON recipe_slot_candidate (dataset_id, target_domain, target_id, recipe_id, slot_key);
CREATE INDEX IF NOT EXISTS idx_nesql_ore_dictionary_entry_item
  ON ore_dictionary_entry (dataset_id, item_variant_id, dictionary_name);
CREATE INDEX IF NOT EXISTS idx_nesql_ore_dictionary_entry_name_order
  ON ore_dictionary_entry (dataset_id, dictionary_name, list_index, item_variant_id);
CREATE INDEX IF NOT EXISTS idx_nesql_fluid_container_fluid_order
  ON fluid_container (dataset_id, fluid_id, amount, item_variant_id);
CREATE INDEX IF NOT EXISTS idx_nesql_fluid_container_item
  ON fluid_container (dataset_id, item_variant_id, fluid_id);
CREATE INDEX IF NOT EXISTS idx_nesql_fluid_block_item
  ON fluid_block (dataset_id, item_variant_id, fluid_id);
CREATE INDEX IF NOT EXISTS idx_nesql_recipe_metadata_key
  ON recipe_metadata (dataset_id, metadata_key);
CREATE INDEX IF NOT EXISTS idx_nesql_recipe_metadata_recipe
  ON recipe_metadata (dataset_id, recipe_id);
CREATE INDEX IF NOT EXISTS idx_nesql_recipe_filter_tag_lookup
  ON recipe_filter_tag (dataset_id, tag_key, tag_value, recipe_id);
CREATE INDEX IF NOT EXISTS idx_nesql_mob_drop_item
  ON mob_drop (dataset_id, item_variant_id, mob_variant_id);
CREATE INDEX IF NOT EXISTS idx_nesql_quest_line_entry_quest
  ON quest_line_entry (dataset_id, quest_id);
CREATE INDEX IF NOT EXISTS idx_nesql_quest_dependency_required
  ON quest_dependency (dataset_id, required_quest_id, quest_id);
CREATE INDEX IF NOT EXISTS idx_nesql_quest_task_quest_order
  ON quest_task (dataset_id, quest_id, list_index);
CREATE INDEX IF NOT EXISTS idx_nesql_quest_reward_quest_order
  ON quest_reward (dataset_id, quest_id, list_index);
CREATE INDEX IF NOT EXISTS idx_nesql_aspect_item_item
  ON aspect_item (dataset_id, item_variant_id, aspect_id);

-- Read-only convenience views for frontend and analysis consumers.
DROP VIEW IF EXISTS v_gt_underground_fluid_browser;
DROP VIEW IF EXISTS v_quest_reward_item_browser;
DROP VIEW IF EXISTS v_quest_task_item_browser;
DROP VIEW IF EXISTS v_quest_line_edge_browser;
DROP VIEW IF EXISTS v_quest_line_node_browser;
DROP VIEW IF EXISTS v_quest_browser;
DROP VIEW IF EXISTS v_quest_line_browser;
DROP VIEW IF EXISTS v_recipe_lookup_count;
DROP VIEW IF EXISTS v_fluid_block_browser;
DROP VIEW IF EXISTS v_item_aspect_browser;
DROP VIEW IF EXISTS v_fluid_container_browser;
DROP VIEW IF EXISTS v_mob_drop_browser;
DROP VIEW IF EXISTS v_mob_mod_option;
DROP VIEW IF EXISTS v_fluid_mod_option;
DROP VIEW IF EXISTS v_item_mod_option;
DROP VIEW IF EXISTS v_recipe_lookup_voltage_tier;
DROP VIEW IF EXISTS v_recipe_lookup_breakdown;
DROP VIEW IF EXISTS v_recipe_category_voltage_tier;
DROP VIEW IF EXISTS v_recipe_category_applicable_item_browser;
DROP VIEW IF EXISTS v_recipe_category_catalyst_browser;
DROP VIEW IF EXISTS v_recipe_lookup_recipe_browser;
DROP VIEW IF EXISTS v_recipe_lookup_entry;
DROP VIEW IF EXISTS v_recipe_search_entry;
DROP VIEW IF EXISTS v_recipe_slot_browser;
DROP VIEW IF EXISTS v_recipe_browser;
DROP VIEW IF EXISTS v_recipe_lookup_detail;
DROP VIEW IF EXISTS v_recipe_item_slot;
DROP VIEW IF EXISTS v_recipe_fluid_slot;
DROP VIEW IF EXISTS v_item_list;

CREATE OR REPLACE VIEW v_item_mod_option AS
SELECT
  used.dataset_id,
  used.mod_id,
  COALESCE(m.name, used.mod_id) AS name
FROM (
  SELECT DISTINCT dataset_id, mod_id
  FROM item
  WHERE mod_id <> ''
) used
LEFT JOIN mod m
  ON m.dataset_id = used.dataset_id
 AND m.mod_id = used.mod_id;

CREATE OR REPLACE VIEW v_item_ref AS
SELECT
  iv.dataset_id,
  iv.item_variant_id,
  iv.item_id,
  i.mod_id,
  COALESCE(m.name, i.mod_id) AS mod_name,
  iv.display_name,
  iv.tooltip_text,
  a.path AS asset_path
FROM item_variant iv
JOIN item i
  ON i.dataset_id = iv.dataset_id
 AND i.item_id = iv.item_id
LEFT JOIN mod m
  ON m.dataset_id = i.dataset_id
 AND m.mod_id = i.mod_id
LEFT JOIN asset a
  ON a.dataset_id = iv.dataset_id
 AND a.owner_type = 'item_variant'
 AND a.owner_id = iv.item_variant_id
 AND a.kind = 'item_icon';

CREATE OR REPLACE VIEW v_item_detail AS
SELECT
  iv.dataset_id,
  iv.item_variant_id,
  iv.item_id,
  i.mod_id,
  COALESCE(m.name, i.mod_id) AS mod_name,
  i.registry_name,
  i.unlocalized_name,
  i.max_stack_size,
  i.max_damage,
  iv.damage,
  iv.nbt_text,
  iv.chemical_expression,
  iv.display_name,
  iv.tooltip_text
FROM item_variant iv
JOIN item i
  ON i.dataset_id = iv.dataset_id
 AND i.item_id = iv.item_id
LEFT JOIN mod m
  ON m.dataset_id = i.dataset_id
 AND m.mod_id = i.mod_id;

CREATE OR REPLACE VIEW v_item_list AS
SELECT
  p.dataset_id,
  p.item_variant_id,
  iv.item_id,
  i.mod_id,
  COALESCE(m.name, i.mod_id) AS mod_name,
  i.registry_name,
  iv.damage,
  iv.display_name,
  a.path AS asset_path,
  p.list_index
FROM item_list_entry pJOIN item_variant iv
  ON iv.dataset_id = p.dataset_id
 AND iv.item_variant_id = p.item_variant_id
JOIN item i
  ON i.dataset_id = iv.dataset_id
 AND i.item_id = iv.item_id
LEFT JOIN mod m
  ON m.dataset_id = i.dataset_id
 AND m.mod_id = i.mod_id
LEFT JOIN asset a
  ON a.dataset_id = iv.dataset_id
 AND a.owner_type = 'item_variant'
 AND a.owner_id = iv.item_variant_id
 AND a.kind = 'item_icon';

CREATE OR REPLACE VIEW v_fluid_mod_option AS
SELECT
  used.dataset_id,
  used.mod_id,
  COALESCE(m.name, used.mod_id) AS name
FROM (
  SELECT DISTINCT dataset_id, mod_id
  FROM fluid
  WHERE mod_id <> ''
) used
LEFT JOIN mod m
  ON m.dataset_id = used.dataset_id
 AND m.mod_id = used.mod_id;

CREATE OR REPLACE VIEW v_fluid_ref AS
SELECT
  f.dataset_id,
  f.fluid_id,
  f.mod_id,
  COALESCE(m.name, f.mod_id) AS mod_name,
  f.display_name,
  f.gaseous,
  f.temperature,
  a.path AS asset_path
FROM fluid f
LEFT JOIN mod m
  ON m.dataset_id = f.dataset_id
 AND m.mod_id = f.mod_id
LEFT JOIN asset a
  ON a.dataset_id = f.dataset_id
 AND a.owner_type = 'fluid'
 AND a.owner_id = f.fluid_id
 AND a.kind = 'fluid_icon';

CREATE OR REPLACE VIEW v_fluid_detail AS
SELECT
  f.dataset_id,
  f.fluid_id,
  f.mod_id,
  COALESCE(m.name, f.mod_id) AS mod_name,
  f.registry_name,
  f.unlocalized_name,
  f.display_name,
  f.gaseous,
  f.density,
  f.temperature,
  f.viscosity,
  f.luminosity,
  f.chemical_expression
FROM fluid f
LEFT JOIN mod m
  ON m.dataset_id = f.dataset_id
 AND m.mod_id = f.mod_id;

CREATE OR REPLACE VIEW v_fluid_list AS
SELECT
  f.dataset_id,
  f.fluid_id,
  f.mod_id,
  COALESCE(m.name, f.mod_id) AS mod_name,
  f.registry_name,
  f.display_name,
  f.gaseous,
  f.density,
  f.temperature,
  f.viscosity,
  f.luminosity,
  a.path AS asset_path
FROM fluid f
LEFT JOIN mod m
  ON m.dataset_id = f.dataset_id
 AND m.mod_id = f.mod_id
LEFT JOIN asset a
  ON a.dataset_id = f.dataset_id
 AND a.owner_type = 'fluid'
 AND a.owner_id = f.fluid_id
 AND a.kind = 'fluid_icon';

CREATE OR REPLACE VIEW v_mob_variant_browser AS
SELECT
  mv.dataset_id,
  mv.mob_variant_id,
  mv.mob_id,
  m.mod_id,
  m.entity_name,
  m.display_name,
  m.width,
  m.height,
  m.max_health,
  m.armor,
  m.immune_to_fire,
  m.leashable,
  mv.nbt_hash,
  mv.nbt_text,
  a.path AS asset_path,
  COALESCE(mod.name, m.mod_id) AS mod_name
FROM mob_variant mv
JOIN mob m
  ON m.dataset_id = mv.dataset_id
 AND m.mob_id = mv.mob_id
LEFT JOIN mod
  ON mod.dataset_id = m.dataset_id
 AND mod.mod_id = m.mod_id
LEFT JOIN asset a
  ON a.dataset_id = mv.dataset_id
 AND a.owner_type = 'mob_variant'
 AND a.owner_id = mv.mob_variant_id
 AND a.kind = 'mob_render';

CREATE OR REPLACE VIEW v_mob_mod_option AS
SELECT
  used.dataset_id,
  used.mod_id,
  COALESCE(m.name, used.mod_id) AS name
FROM (
  SELECT DISTINCT dataset_id, mod_id
  FROM v_mob_variant_browser
  WHERE mod_id <> ''
) used
LEFT JOIN mod m
  ON m.dataset_id = used.dataset_id
 AND m.mod_id = used.mod_id;

CREATE OR REPLACE VIEW v_mob_drop_browser AS
SELECT
  d.dataset_id,
  d.mob_variant_id,
  d.drop_type,
  d.list_index,
  d.item_variant_id,
  d.stack_size,
  d.probability,
  d.lootable,
  d.player_only
FROM mob_drop d;

CREATE OR REPLACE VIEW v_fluid_container_browser AS
SELECT
  fc.dataset_id,
  fc.fluid_id,
  fc.item_variant_id,
  fc.amount
FROM fluid_container fc;

CREATE OR REPLACE VIEW v_item_aspect_browser AS
SELECT
  ai.dataset_id,
  ai.item_variant_id,
  a.aspect_id,
  a.name,
  a.description,
  a.primal,
  ai.amount,
  a.icon_item_variant_id,
  iv.asset_path AS icon_asset_path
FROM aspect_item ai
JOIN aspect a
  ON a.dataset_id = ai.dataset_id
 AND a.aspect_id = ai.aspect_id
LEFT JOIN v_item_ref iv
  ON iv.dataset_id = a.dataset_id
 AND iv.item_variant_id = a.icon_item_variant_id;

CREATE OR REPLACE VIEW v_fluid_block_browser AS
SELECT
  fb.dataset_id,
  fb.fluid_id,
  fb.item_variant_id
FROM fluid_block fb;

CREATE OR REPLACE VIEW v_recipe_lookup_count AS
SELECT
  dataset_id,
  target_domain,
  target_id,
  lookup_kind,
  COUNT(DISTINCT recipe_id) AS recipe_count
FROM v_recipe_lookup_entry
GROUP BY dataset_id, target_domain, target_id, lookup_kind;

CREATE OR REPLACE VIEW v_quest_line_browser AS
SELECT
  ql.dataset_id,
  ql.quest_line_id,
  ql.name,
  ql.description,
  ql.visibility,
  ql.order_index,
  ql.icon_item_variant_id,
  iv.asset_path AS icon_asset_path,
  COUNT(e.quest_id) AS quest_count
FROM quest_line ql
LEFT JOIN quest_line_entry e
  ON e.dataset_id = ql.dataset_id
 AND e.quest_line_id = ql.quest_line_id
LEFT JOIN v_item_ref iv
  ON iv.dataset_id = ql.dataset_id
 AND iv.item_variant_id = ql.icon_item_variant_id
GROUP BY ql.dataset_id, ql.quest_line_id, ql.name, ql.description, ql.visibility,
         ql.order_index, ql.icon_item_variant_id, iv.asset_path;

CREATE OR REPLACE VIEW v_quest_browser AS
SELECT
  q.dataset_id,
  q.quest_id,
  q.name,
  q.description,
  q.visibility,
  q.repeat_time,
  q.quest_logic,
  q.task_logic,
  q.icon_item_variant_id,
  iv.asset_path AS icon_asset_path
FROM quest q
LEFT JOIN v_item_ref iv
  ON iv.dataset_id = q.dataset_id
 AND iv.item_variant_id = q.icon_item_variant_id;

CREATE OR REPLACE VIEW v_quest_line_node_browser AS
SELECT
  e.dataset_id,
  e.quest_line_id,
  q.quest_id,
  q.name,
  q.description,
  q.visibility,
  q.repeat_time,
  q.icon_item_variant_id,
  q.icon_asset_path,
  e.pos_x,
  e.pos_y,
  e.size_x,
  e.size_y
FROM quest_line_entry e
JOIN v_quest_browser q
  ON q.dataset_id = e.dataset_id
 AND q.quest_id = e.quest_id;

CREATE OR REPLACE VIEW v_quest_line_edge_browser AS
SELECT
  d.dataset_id,
  e.quest_line_id,
  d.quest_id,
  d.required_quest_id
FROM quest_dependency d
JOIN quest_line_entry e
  ON e.dataset_id = d.dataset_id
 AND e.quest_id = d.quest_id
JOIN quest_line_entry r
  ON r.dataset_id = d.dataset_id
 AND r.quest_line_id = e.quest_line_id
 AND r.quest_id = d.required_quest_id;

CREATE OR REPLACE VIEW v_recipe_category_base AS
SELECT
  rc.dataset_id,
  rc.category_id,
  rc.display_name,
  rc.mod_id,
  COALESCE(m.name, '') AS mod_name,
  rendered_icon.path AS icon_asset_path,
  rendered_background.path AS background_asset_path,
  rc.canvas_width,
  rc.canvas_height
FROM recipe_category rc
LEFT JOIN mod m
  ON m.dataset_id = rc.dataset_id
 AND m.mod_id = rc.mod_id
LEFT JOIN asset rendered_icon
  ON rendered_icon.dataset_id = rc.dataset_id
 AND rendered_icon.owner_type = 'category'
 AND rendered_icon.owner_id = rc.category_id
 AND rendered_icon.kind = 'recipe_category_icon'
LEFT JOIN asset rendered_background
  ON rendered_background.dataset_id = rc.dataset_id
 AND rendered_background.owner_type = 'category'
 AND rendered_background.owner_id = rc.category_id
 AND rendered_background.kind = 'recipe_handler_background';

CREATE OR REPLACE VIEW v_recipe_category_counts AS
SELECT
  c.dataset_id,
  c.category_id,
  COALESCE(r.recipe_count, 0) AS recipe_count,
  COALESCE(m.applicable_item_count, 0) AS applicable_item_count
FROM recipe_category c
LEFT JOIN (
  SELECT dataset_id, category_id, COUNT(*) AS recipe_count
  FROM recipe
  GROUP BY dataset_id, category_id
) r
  ON r.dataset_id = c.dataset_id
 AND r.category_id = c.category_id
LEFT JOIN (
  SELECT dataset_id, category_id, COUNT(*) AS applicable_item_count
  FROM recipe_category_catalyst
  GROUP BY dataset_id, category_id
) m
  ON m.dataset_id = c.dataset_id
 AND m.category_id = c.category_id;

CREATE OR REPLACE VIEW v_recipe_category_browser AS
SELECT
  b.*,
  c.recipe_count,
  c.applicable_item_count
FROM v_recipe_category_base b
JOIN v_recipe_category_counts c
  ON c.dataset_id = b.dataset_id
 AND c.category_id = b.category_id;

CREATE OR REPLACE VIEW v_recipe_browser AS
SELECT
  r.dataset_id,
  r.recipe_id,
  r.category_id,
  rc.display_name AS category_display_name,
  r.display_order,
  rc.mod_id,
  COALESCE(m.name, '') AS mod_name
FROM recipe r
JOIN recipe_category rc
  ON rc.dataset_id = r.dataset_id
 AND rc.category_id = r.category_id
LEFT JOIN mod m
  ON m.dataset_id = rc.dataset_id
 AND m.mod_id = rc.mod_id;

CREATE OR REPLACE VIEW v_recipe_slot_browser AS
SELECT
  c.dataset_id,
  c.recipe_id,
  r.category_id,
  l.role,
  c.slot_key,
  l.x,
  l.y,
  l.width,
  l.height,
  l.display_order AS slot_display_order,
  c.candidate_order,
  c.target_domain,
  c.target_id,
  c.amount,
  c.probability
FROM recipe_slot_candidate c
JOIN recipe r
  ON r.dataset_id = c.dataset_id
 AND r.recipe_id = c.recipe_id
JOIN recipe_slot_layout l
  ON l.dataset_id = r.dataset_id
 AND l.category_id = r.category_id
 AND l.slot_key = c.slot_key;

CREATE OR REPLACE VIEW v_recipe_search_entry AS
SELECT
  c.dataset_id,
  c.recipe_id,
  r.category_id,
  c.target_domain,
  c.target_id,
  COALESCE(iv.display_name, fv.display_name) AS display_name,
  lower(
    coalesce(iv.display_name, fv.display_name, '') || ' ' ||
    coalesce(c.target_id, '')
  ) AS search_text
FROM recipe_slot_candidate c
JOIN recipe r
  ON r.dataset_id = c.dataset_id
 AND r.recipe_id = c.recipe_id
LEFT JOIN v_item_ref iv
  ON c.target_domain = 'item'
 AND iv.dataset_id = c.dataset_id
 AND iv.item_variant_id = c.target_id
LEFT JOIN v_fluid_ref fv
  ON c.target_domain = 'fluid'
 AND fv.dataset_id = c.dataset_id
 AND fv.fluid_id = c.target_id;

CREATE MATERIALIZED VIEW IF NOT EXISTS item_search_document AS
SELECT
  p.dataset_id,
  p.item_variant_id,
  iv.item_id,
  i.mod_id,
  p.list_index,
  lower(i.search_text || ' ' || iv.search_text || ' ' || i.mod_id || ' ' || coalesce(od.dictionary_names, '')) AS search_text
FROM item_list_entry p
JOIN item_variant iv
  ON iv.dataset_id = p.dataset_id
 AND iv.item_variant_id = p.item_variant_id
JOIN item i
  ON i.dataset_id = iv.dataset_id
 AND i.item_id = iv.item_id
LEFT JOIN (
  SELECT
    dataset_id,
    item_variant_id,
    string_agg(DISTINCT dictionary_name, ' ') AS dictionary_names
  FROM ore_dictionary_entry
  GROUP BY dataset_id, item_variant_id
) od
  ON od.dataset_id = p.dataset_id
 AND od.item_variant_id = p.item_variant_id
WITH DATA;

CREATE UNIQUE INDEX IF NOT EXISTS idx_nesql_item_search_document_unique
  ON item_search_document (dataset_id, item_variant_id);
CREATE INDEX IF NOT EXISTS idx_nesql_item_search_document_order
  ON item_search_document (dataset_id, list_index, item_variant_id);
CREATE INDEX IF NOT EXISTS idx_nesql_item_search_document_mod_order
  ON item_search_document (dataset_id, mod_id, list_index, item_variant_id);
CREATE INDEX IF NOT EXISTS idx_nesql_item_search_document_trgm
  ON item_search_document USING gin (search_text gin_trgm_ops);

CREATE MATERIALIZED VIEW IF NOT EXISTS fluid_search_document AS
SELECT
  f.dataset_id,
  f.fluid_id,
  f.mod_id,
  f.display_name,
  lower(f.search_text || ' ' || f.mod_id) AS search_text
FROM fluid f
WITH DATA;

CREATE UNIQUE INDEX IF NOT EXISTS idx_nesql_fluid_search_document_unique
  ON fluid_search_document (dataset_id, fluid_id);
CREATE INDEX IF NOT EXISTS idx_nesql_fluid_search_document_order
  ON fluid_search_document (dataset_id, mod_id, display_name, fluid_id);
CREATE INDEX IF NOT EXISTS idx_nesql_fluid_search_document_trgm
  ON fluid_search_document USING gin (search_text gin_trgm_ops);

CREATE MATERIALIZED VIEW IF NOT EXISTS mob_search_document AS
SELECT
  mv.dataset_id,
  mv.mob_variant_id,
  mv.mob_id,
  m.mod_id,
  m.display_name,
  m.entity_name,
  lower(mv.mob_variant_id || ' ' || mv.mob_id || ' ' || m.display_name || ' ' || m.entity_name || ' ' || m.mod_id) AS search_text
FROM mob_variant mv
JOIN mob m
  ON m.dataset_id = mv.dataset_id
 AND m.mob_id = mv.mob_id
WITH DATA;

CREATE UNIQUE INDEX IF NOT EXISTS idx_nesql_mob_search_document_unique
  ON mob_search_document (dataset_id, mob_variant_id);
CREATE INDEX IF NOT EXISTS idx_nesql_mob_search_document_order
  ON mob_search_document (dataset_id, mod_id, display_name, mob_variant_id);
CREATE INDEX IF NOT EXISTS idx_nesql_mob_search_document_trgm
  ON mob_search_document USING gin (search_text gin_trgm_ops);

CREATE MATERIALIZED VIEW IF NOT EXISTS recipe_search_document AS
SELECT
  r.dataset_id,
  r.recipe_id,
  r.category_id,
  lower(
    r.recipe_id || ' ' ||
    r.category_id || ' ' ||
    coalesce(string_agg(DISTINCT part.search_text, ' '), '')
  ) AS search_text
FROM recipe r
LEFT JOIN (
  SELECT
    c.dataset_id,
    c.recipe_id,
    iv.search_text || ' ' || i.search_text AS search_text
  FROM recipe_slot_candidate c
  JOIN item_variant iv
    ON iv.dataset_id = c.dataset_id
   AND iv.item_variant_id = c.target_id
  JOIN item i
    ON i.dataset_id = iv.dataset_id
   AND i.item_id = iv.item_id
  WHERE c.target_domain = 'item'

  UNION ALL

  SELECT
    c.dataset_id,
    c.recipe_id,
    f.search_text AS search_text
  FROM recipe_slot_candidate c
  JOIN fluid f
    ON f.dataset_id = c.dataset_id
   AND f.fluid_id = c.target_id
  WHERE c.target_domain = 'fluid'
) part
  ON part.dataset_id = r.dataset_id
 AND part.recipe_id = r.recipe_id
GROUP BY r.dataset_id, r.recipe_id, r.category_id
WITH DATA;

CREATE UNIQUE INDEX IF NOT EXISTS recipe_search_document_pkey
  ON recipe_search_document (dataset_id, recipe_id);
CREATE INDEX IF NOT EXISTS idx_nesql_recipe_search_document_category_recipe
  ON recipe_search_document (dataset_id, category_id, recipe_id);
CREATE INDEX IF NOT EXISTS idx_nesql_recipe_search_document_trgm
  ON recipe_search_document USING gin (search_text gin_trgm_ops);

CREATE OR REPLACE VIEW v_recipe_lookup_entry AS
SELECT
  c.dataset_id,
  c.target_domain,
  c.target_id,
  l.role AS lookup_kind,
  c.recipe_id,
  r.category_id,
  c.slot_key,
  l.display_order AS slot_display_order,
  c.candidate_order,
  c.amount,
  c.probability,
  r.display_order AS recipe_display_order
FROM recipe_slot_candidate c
JOIN recipe r
  ON r.dataset_id = c.dataset_id
 AND r.recipe_id = c.recipe_id
JOIN recipe_slot_layout l
  ON l.dataset_id = r.dataset_id
 AND l.category_id = r.category_id
 AND l.slot_key = c.slot_key;

CREATE OR REPLACE VIEW v_recipe_lookup_recipe_browser AS
SELECT
  e.dataset_id,
  e.target_domain,
  e.target_id,
  e.lookup_kind,
  e.recipe_id,
  e.category_id,
  MIN(e.slot_display_order) AS display_order,
  e.recipe_display_order
FROM v_recipe_lookup_entry e
GROUP BY e.dataset_id, e.target_domain, e.target_id, e.lookup_kind,
         e.recipe_id, e.category_id, e.recipe_display_order;

CREATE OR REPLACE VIEW v_recipe_category_catalyst_browser AS
SELECT
  m.dataset_id,
  m.category_id,
  m.item_variant_id,
  iv.display_name,
  iv.asset_path,
  m.display_order
FROM recipe_category_catalyst m
LEFT JOIN v_item_ref iv
  ON iv.dataset_id = m.dataset_id
 AND iv.item_variant_id = m.item_variant_id;

CREATE OR REPLACE VIEW v_recipe_category_voltage_tier AS
SELECT
  r.dataset_id,
  r.category_id,
  ft.tag_value AS voltage_tier,
  COUNT(*) AS recipe_count,
  MIN(ft.sort_value) AS min_voltage
FROM recipe r
JOIN recipe_filter_tag ft
  ON ft.dataset_id = r.dataset_id
 AND ft.recipe_id = r.recipe_id
 AND ft.tag_key = 'voltage_tier'
GROUP BY r.dataset_id, r.category_id, ft.tag_value;

CREATE OR REPLACE VIEW v_recipe_lookup_voltage_tier AS
SELECT
  l.dataset_id,
  l.target_id,
  l.lookup_kind,
  l.category_id,
  ft.tag_value AS voltage_tier,
  COUNT(*) AS recipe_count,
  MIN(ft.sort_value) AS min_voltage
FROM v_recipe_lookup_recipe_browser l
JOIN recipe_filter_tag ft
  ON ft.dataset_id = l.dataset_id
 AND ft.recipe_id = l.recipe_id
 AND ft.tag_key = 'voltage_tier'
GROUP BY l.dataset_id, l.target_id, l.lookup_kind, l.category_id, ft.tag_value;

CREATE OR REPLACE VIEW v_recipe_lookup_breakdown AS
SELECT
  l.dataset_id,
  l.target_id,
  l.lookup_kind,
  l.category_id,
  c.display_name,
  c.icon_asset_path,
  COUNT(DISTINCT l.recipe_id) AS recipe_count
FROM v_recipe_lookup_recipe_browser l
JOIN v_recipe_category_browser c
  ON c.dataset_id = l.dataset_id
 AND c.category_id = l.category_id
GROUP BY l.dataset_id, l.target_id, l.lookup_kind, l.category_id,
         c.display_name, c.icon_asset_path;

-- NEI Custom Diagram tables
CREATE TABLE IF NOT EXISTS diagram_group (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  group_id TEXT NOT NULL,
  display_name TEXT NOT NULL DEFAULT '',
  description TEXT NOT NULL DEFAULT '',
  icon_item_variant_id TEXT NOT NULL DEFAULT '',
  diagrams_per_page INTEGER NOT NULL DEFAULT 0,
  ignore_nbt BOOLEAN NOT NULL DEFAULT FALSE,
  default_visibility TEXT NOT NULL DEFAULT '',
  PRIMARY KEY (dataset_id, group_id)
);

CREATE TABLE IF NOT EXISTS diagram (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  group_id TEXT NOT NULL,
  diagram_id TEXT NOT NULL,
  display_order INTEGER NOT NULL,
  width INTEGER NOT NULL DEFAULT 0,
  height INTEGER NOT NULL DEFAULT 0,
  PRIMARY KEY (dataset_id, group_id, diagram_id)
);

CREATE TABLE IF NOT EXISTS diagram_slot (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  group_id TEXT NOT NULL,
  diagram_id TEXT NOT NULL,
  slot_key TEXT NOT NULL,
  slot_group_key TEXT NOT NULL DEFAULT '',
  slot_index INTEGER NOT NULL DEFAULT 0,
  x INTEGER NOT NULL,
  y INTEGER NOT NULL,
  width INTEGER NOT NULL,
  height INTEGER NOT NULL,
  tooltip_text TEXT NOT NULL DEFAULT '',
  PRIMARY KEY (dataset_id, group_id, diagram_id, slot_key)
);

CREATE TABLE IF NOT EXISTS diagram_slot_entry (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  group_id TEXT NOT NULL,
  diagram_id TEXT NOT NULL,
  slot_key TEXT NOT NULL,
  entry_index INTEGER NOT NULL,
  component_type TEXT NOT NULL,
  item_variant_id TEXT NOT NULL DEFAULT '',
  fluid_id TEXT NOT NULL DEFAULT '',
  stack_size INTEGER NOT NULL DEFAULT 0,
  additional_info TEXT NOT NULL DEFAULT '',
  tooltip_text TEXT NOT NULL DEFAULT '',
  PRIMARY KEY (dataset_id, group_id, diagram_id, slot_key, entry_index)
);

CREATE TABLE IF NOT EXISTS diagram_label (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  group_id TEXT NOT NULL,
  diagram_id TEXT NOT NULL,
  label_index INTEGER NOT NULL,
  x INTEGER NOT NULL,
  y INTEGER NOT NULL,
  text TEXT NOT NULL DEFAULT '',
  icon_item_variant_id TEXT NOT NULL DEFAULT '',
  PRIMARY KEY (dataset_id, group_id, diagram_id, label_index)
);

CREATE TABLE IF NOT EXISTS diagram_line (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  group_id TEXT NOT NULL,
  diagram_id TEXT NOT NULL,
  line_index INTEGER NOT NULL,
  segment_kind TEXT NOT NULL,
  from_x INTEGER NOT NULL,
  from_y INTEGER NOT NULL,
  to_x INTEGER NOT NULL,
  to_y INTEGER NOT NULL,
  colour INTEGER NOT NULL DEFAULT 0,
  PRIMARY KEY (dataset_id, group_id, diagram_id, line_index)
);

CREATE TABLE IF NOT EXISTS diagram_match (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  group_id TEXT NOT NULL,
  diagram_id TEXT NOT NULL,
  recipe_type TEXT NOT NULL,
  component_type TEXT NOT NULL,
  item_variant_id TEXT NOT NULL DEFAULT '',
  fluid_id TEXT NOT NULL DEFAULT '',
  PRIMARY KEY (dataset_id, group_id, diagram_id, recipe_type, component_type, item_variant_id, fluid_id)
);

CREATE INDEX IF NOT EXISTS idx_nesql_diagram_match_item
  ON diagram_match (dataset_id, item_variant_id, recipe_type);
CREATE INDEX IF NOT EXISTS idx_nesql_diagram_match_fluid
  ON diagram_match (dataset_id, fluid_id, recipe_type);
CREATE INDEX IF NOT EXISTS idx_nesql_diagram_by_group
  ON diagram (dataset_id, group_id, display_order);

-- GT NEI Ore Plugin tables
CREATE TABLE IF NOT EXISTS gt_dimension_display (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  dimension TEXT NOT NULL,
  full_name TEXT NOT NULL DEFAULT '',
  display_name TEXT NOT NULL DEFAULT '',
  display_abbr TEXT NOT NULL DEFAULT '',
  icon_item_variant_id TEXT NOT NULL DEFAULT '',
  sort_order INTEGER NOT NULL DEFAULT 2147483647,
  PRIMARY KEY (dataset_id, dimension)
);

CREATE TABLE IF NOT EXISTS gt_ore_vein (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  vein_name TEXT NOT NULL,
  display_name TEXT NOT NULL DEFAULT '',
  weight INTEGER NOT NULL DEFAULT 0,
  size INTEGER NOT NULL DEFAULT 0,
  density INTEGER NOT NULL DEFAULT 0,
  height_min INTEGER NOT NULL DEFAULT 0,
  height_max INTEGER NOT NULL DEFAULT 0,
  PRIMARY KEY (dataset_id, vein_name)
);

CREATE TABLE IF NOT EXISTS gt_ore_vein_layer (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  vein_name TEXT NOT NULL,
  layer TEXT NOT NULL,
  material_name TEXT NOT NULL DEFAULT '',
  ore_meta INTEGER NOT NULL DEFAULT 0,
  block_item_variant_id TEXT NOT NULL DEFAULT '',
  PRIMARY KEY (dataset_id, vein_name, layer)
);

CREATE TABLE IF NOT EXISTS gt_ore_vein_layer_variant (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  vein_name TEXT NOT NULL,
  layer TEXT NOT NULL,
  variant_index INTEGER NOT NULL,
  item_variant_id TEXT NOT NULL DEFAULT '',
  PRIMARY KEY (dataset_id, vein_name, layer, variant_index)
);

CREATE TABLE IF NOT EXISTS gt_ore_vein_dimension (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  vein_name TEXT NOT NULL,
  dimension TEXT NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT FALSE,
  display_abbr TEXT NOT NULL DEFAULT '',
  PRIMARY KEY (dataset_id, vein_name, dimension)
);

CREATE TABLE IF NOT EXISTS gt_ore_small (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  ore_gen_name TEXT NOT NULL,
  ore_meta INTEGER NOT NULL DEFAULT 0,
  material_name TEXT NOT NULL DEFAULT '',
  amount_per_chunk INTEGER NOT NULL DEFAULT 0,
  height_min INTEGER NOT NULL DEFAULT 0,
  height_max INTEGER NOT NULL DEFAULT 0,
  small_ore_item_variant_id TEXT NOT NULL DEFAULT '',
  dust_item_variant_id TEXT NOT NULL DEFAULT '',
  PRIMARY KEY (dataset_id, ore_gen_name)
);

CREATE TABLE IF NOT EXISTS gt_ore_small_variant (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  ore_gen_name TEXT NOT NULL,
  variant_index INTEGER NOT NULL,
  small_ore_item_variant_id TEXT NOT NULL DEFAULT '',
  dust_item_variant_id TEXT NOT NULL DEFAULT '',
  PRIMARY KEY (dataset_id, ore_gen_name, variant_index)
);

CREATE TABLE IF NOT EXISTS gt_ore_small_drop (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  ore_gen_name TEXT NOT NULL,
  drop_index INTEGER NOT NULL,
  item_variant_id TEXT NOT NULL DEFAULT '',
  PRIMARY KEY (dataset_id, ore_gen_name, drop_index)
);

CREATE TABLE IF NOT EXISTS gt_ore_small_dimension (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  ore_gen_name TEXT NOT NULL,
  dimension TEXT NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT FALSE,
  display_abbr TEXT NOT NULL DEFAULT '',
  PRIMARY KEY (dataset_id, ore_gen_name, dimension)
);

CREATE TABLE IF NOT EXISTS gt_underground_fluid (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  fluid_id TEXT NOT NULL,
  dimension TEXT NOT NULL,
  chance INTEGER NOT NULL DEFAULT 0,
  min_amount INTEGER NOT NULL DEFAULT 0,
  max_amount INTEGER NOT NULL DEFAULT 0,
  PRIMARY KEY (dataset_id, fluid_id, dimension)
);

CREATE TABLE IF NOT EXISTS gt_bartworks_ore (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  entry_id TEXT NOT NULL,
  entry_type TEXT NOT NULL,
  worldgen_name TEXT NOT NULL DEFAULT '',
  dimension TEXT NOT NULL DEFAULT '',
  dimension_display_name TEXT NOT NULL DEFAULT '',
  result_item_variant_id TEXT NOT NULL DEFAULT '',
  result_display_name TEXT NOT NULL DEFAULT '',
  height_min INTEGER NOT NULL DEFAULT 0,
  height_max INTEGER NOT NULL DEFAULT 0,
  weight INTEGER NOT NULL DEFAULT 0,
  density INTEGER NOT NULL DEFAULT 0,
  size INTEGER NOT NULL DEFAULT 0,
  amount_per_chunk INTEGER NOT NULL DEFAULT 0,
  PRIMARY KEY (dataset_id, entry_id)
);

CREATE TABLE IF NOT EXISTS gt_bartworks_ore_layer (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  entry_id TEXT NOT NULL,
  layer TEXT NOT NULL,
  layer_index INTEGER NOT NULL,
  ore_meta INTEGER NOT NULL DEFAULT 0,
  bartworks_ore BOOLEAN NOT NULL DEFAULT FALSE,
  item_variant_id TEXT NOT NULL DEFAULT '',
  display_name TEXT NOT NULL DEFAULT '',
  PRIMARY KEY (dataset_id, entry_id, layer)
);

CREATE INDEX IF NOT EXISTS idx_nesql_gt_dimension_display_order
  ON gt_dimension_display (dataset_id, sort_order, dimension);
CREATE INDEX IF NOT EXISTS idx_nesql_gt_ore_vein_display
  ON gt_ore_vein (dataset_id, display_name, vein_name);
CREATE INDEX IF NOT EXISTS idx_nesql_gt_ore_vein_dimension_filter
  ON gt_ore_vein_dimension (dataset_id, dimension, enabled, vein_name);
CREATE INDEX IF NOT EXISTS idx_nesql_gt_ore_vein_layer_material
  ON gt_ore_vein_layer (dataset_id, material_name, vein_name);
CREATE INDEX IF NOT EXISTS idx_nesql_gt_ore_small_material
  ON gt_ore_small (dataset_id, material_name, ore_gen_name);
CREATE INDEX IF NOT EXISTS idx_nesql_gt_ore_small_dimension_filter
  ON gt_ore_small_dimension (dataset_id, dimension, enabled, ore_gen_name);
CREATE INDEX IF NOT EXISTS idx_nesql_gt_underground_fluid_dim
  ON gt_underground_fluid (dataset_id, dimension, fluid_id);
CREATE INDEX IF NOT EXISTS idx_nesql_gt_bartworks_ore_type
  ON gt_bartworks_ore (dataset_id, entry_type, dimension_display_name, result_display_name, entry_id);
CREATE INDEX IF NOT EXISTS idx_nesql_gt_bartworks_ore_dimension
  ON gt_bartworks_ore (dataset_id, dimension, entry_type, result_display_name, entry_id);
CREATE INDEX IF NOT EXISTS idx_nesql_gt_bartworks_ore_layer_order
  ON gt_bartworks_ore_layer (dataset_id, entry_id, layer_index);

CREATE OR REPLACE VIEW v_gt_underground_fluid_browser AS
SELECT
  uf.dataset_id,
  uf.fluid_id,
  uf.dimension,
  uf.chance,
  uf.min_amount,
  uf.max_amount,
  fv.display_name AS fluid_display_name,
  fv.asset_path AS fluid_asset_path,
  dd.full_name AS dimension_full_name,
  dd.display_name AS dimension_display_name,
  dd.display_abbr AS dimension_display_abbr,
  dd.icon_item_variant_id AS dimension_icon_item_variant_id,
  dd.sort_order AS dimension_sort_order
FROM gt_underground_fluid uf
LEFT JOIN v_fluid_ref fv
  ON fv.dataset_id = uf.dataset_id
 AND fv.fluid_id = uf.fluid_id
LEFT JOIN gt_dimension_display dd
  ON dd.dataset_id = uf.dataset_id
 AND dd.dimension = uf.dimension;
