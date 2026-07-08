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

CREATE TABLE IF NOT EXISTS recipe_metadata (
  dataset_id TEXT NOT NULL REFERENCES dataset(dataset_id) ON DELETE CASCADE,
  recipe_id TEXT NOT NULL,
  metadata_key TEXT NOT NULL,
  value_type TEXT NOT NULL,
  value_text TEXT,
  value_json JSONB,
  PRIMARY KEY (dataset_id, recipe_id, metadata_key)
);

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

CREATE OR REPLACE VIEW v_item_browser AS
SELECT
  ile.dataset_id,
  iv.item_variant_id,
  iv.item_id,
  i.mod_id,
  m.name AS mod_name,
  i.registry_name,
  i.unlocalized_name,
  iv.damage,
  iv.nbt_hash,
  iv.display_name,
  iv.tooltip_text,
  iv.chemical_expression,
  ile.list_index,
  a.path AS icon_path,
  a.media_type AS icon_media_type,
  a.width AS icon_width,
  a.height AS icon_height,
  a.metadata_json AS icon_metadata_json
FROM item_list_entry ile
JOIN item_variant iv
  ON iv.dataset_id = ile.dataset_id
 AND iv.item_variant_id = ile.item_variant_id
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

CREATE OR REPLACE VIEW v_fluid_browser AS
SELECT
  f.dataset_id,
  f.fluid_id,
  f.mod_id,
  m.name AS mod_name,
  f.registry_name,
  f.unlocalized_name,
  f.display_name,
  f.chemical_expression,
  f.luminosity,
  f.density,
  f.temperature,
  f.viscosity,
  f.gaseous,
  a.path AS icon_path,
  a.media_type AS icon_media_type,
  a.width AS icon_width,
  a.height AS icon_height,
  a.metadata_json AS icon_metadata_json
FROM fluid f
LEFT JOIN mod m
  ON m.dataset_id = f.dataset_id
 AND m.mod_id = f.mod_id
LEFT JOIN asset a
  ON a.dataset_id = f.dataset_id
 AND a.owner_type = 'fluid'
 AND a.owner_id = f.fluid_id
 AND a.kind = 'fluid_icon';

CREATE OR REPLACE VIEW v_recipe_category_browser AS
SELECT
  rc.dataset_id,
  rc.category_id,
  rc.display_name,
  rc.mod_id,
  m.name AS mod_name,
  (SELECT COUNT(*) FROM recipe r
    WHERE r.dataset_id = rc.dataset_id
      AND r.category_id = rc.category_id) AS recipe_count,
  a.path AS icon_path,
  a.width AS icon_width,
  a.height AS icon_height,
  a.metadata_json AS icon_metadata_json,
  lower(regexp_replace(rc.display_name, '§.', '', 'g'))
    || ' ' || lower(coalesce(m.name, '')) AS search_std,
  lower(coalesce(m.name, '')) AS search_mod
FROM recipe_category rc
LEFT JOIN mod m
  ON m.dataset_id = rc.dataset_id
 AND m.mod_id = rc.mod_id
LEFT JOIN asset a
  ON a.dataset_id = rc.dataset_id
 AND a.owner_type = 'category'
 AND a.owner_id = rc.category_id
 AND a.kind = 'recipe_category_icon';

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_item_list_entry_dataset_list_index
  ON item_list_entry (dataset_id, list_index) INCLUDE (item_variant_id);

CREATE INDEX IF NOT EXISTS idx_item_variant_dataset_item_id
  ON item_variant (dataset_id, item_id);

CREATE INDEX IF NOT EXISTS idx_asset_dataset_owner_kind_id
  ON asset (dataset_id, owner_type, kind, owner_id);

CREATE INDEX IF NOT EXISTS idx_fluid_dataset_display
  ON fluid (dataset_id, display_name, fluid_id);

CREATE INDEX IF NOT EXISTS idx_ore_dictionary_entry_dataset_item_dictionary
  ON ore_dictionary_entry (dataset_id, item_variant_id, dictionary_name);

-- 配方分类列表：名称/模组按 trgm 子串搜索，配方数按分类聚合。
CREATE INDEX IF NOT EXISTS idx_recipe_category_name_trgm
  ON recipe_category USING gin (lower(regexp_replace(display_name, '§.', '', 'g')) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_mod_name_trgm
  ON mod USING gin (lower(name) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_recipe_dataset_category
  ON recipe (dataset_id, category_id);

-- 搜索物化视图：每种搜索能力一个已规范化列（去色码 + 小写 + 域特定处理），
-- 查询期只做 LIKE 子串匹配，命中返回主键 id，展示数据仍走 v_item_browser / v_fluid_browser。
-- 规范化在此固化一次，后端不再现算。行集与展示视图对齐（item 从 item_list_entry 起）。
--   s_std = 标准无前缀组合：名字 + 提示(跳首行) + ID(registry) + 矿典
--   s_mod = 模组名(@)  s_tooltip = 提示(#)  s_ore = 矿典($)  s_id = ID(&)
CREATE MATERIALIZED VIEW mv_item_search AS
SELECT
  b.dataset_id,
  b.item_variant_id,
  b.name_norm || ' ' || b.tooltip_norm || ' ' || b.id_norm || ' ' || b.ore_norm AS s_std,
  b.mod_norm     AS s_mod,
  b.tooltip_norm AS s_tooltip,
  b.ore_norm     AS s_ore,
  b.id_norm      AS s_id
FROM (
  SELECT
    ile.dataset_id,
    iv.item_variant_id,
    lower(regexp_replace(iv.display_name, '§.', '', 'g')) AS name_norm,
    CASE WHEN position(E'\n' in iv.tooltip_text) > 0
         THEN lower(regexp_replace(substring(iv.tooltip_text from position(E'\n' in iv.tooltip_text) + 1), '§.', '', 'g'))
         ELSE '' END AS tooltip_norm,
    lower(i.item_id) AS id_norm,
    lower(coalesce(m.name, '')) AS mod_norm,
    lower(coalesce(ore.names, '')) AS ore_norm
  FROM item_list_entry ile
  JOIN item_variant iv
    ON iv.dataset_id = ile.dataset_id
   AND iv.item_variant_id = ile.item_variant_id
  JOIN item i
    ON i.dataset_id = iv.dataset_id
   AND i.item_id = iv.item_id
  LEFT JOIN mod m
    ON m.dataset_id = i.dataset_id
   AND m.mod_id = i.mod_id
  LEFT JOIN (
    SELECT dataset_id, item_variant_id, string_agg(dictionary_name, ' ') AS names
    FROM ore_dictionary_entry
    GROUP BY dataset_id, item_variant_id
  ) ore
    ON ore.dataset_id = iv.dataset_id
   AND ore.item_variant_id = iv.item_variant_id
) b;

CREATE UNIQUE INDEX IF NOT EXISTS idx_mv_item_search_pk
  ON mv_item_search (dataset_id, item_variant_id);
CREATE INDEX IF NOT EXISTS idx_mv_item_search_std_trgm
  ON mv_item_search USING gin (s_std gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_mv_item_search_mod_trgm
  ON mv_item_search USING gin (s_mod gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_mv_item_search_tooltip_trgm
  ON mv_item_search USING gin (s_tooltip gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_mv_item_search_ore_trgm
  ON mv_item_search USING gin (s_ore gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_mv_item_search_id_trgm
  ON mv_item_search USING gin (s_id gin_trgm_ops);

CREATE MATERIALIZED VIEW mv_fluid_search AS
SELECT
  b.dataset_id,
  b.fluid_id,
  b.name_norm || ' ' || b.id_norm AS s_std,
  b.mod_norm AS s_mod,
  b.id_norm  AS s_id
FROM (
  SELECT
    f.dataset_id,
    f.fluid_id,
    lower(regexp_replace(f.display_name, '§.', '', 'g')) AS name_norm,
    lower(f.fluid_id) AS id_norm,
    lower(coalesce(m.name, '')) AS mod_norm
  FROM fluid f
  LEFT JOIN mod m
    ON m.dataset_id = f.dataset_id
   AND m.mod_id = f.mod_id
) b;

CREATE UNIQUE INDEX IF NOT EXISTS idx_mv_fluid_search_pk
  ON mv_fluid_search (dataset_id, fluid_id);
CREATE INDEX IF NOT EXISTS idx_mv_fluid_search_std_trgm
  ON mv_fluid_search USING gin (s_std gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_mv_fluid_search_mod_trgm
  ON mv_fluid_search USING gin (s_mod gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_mv_fluid_search_id_trgm
  ON mv_fluid_search USING gin (s_id gin_trgm_ops);
