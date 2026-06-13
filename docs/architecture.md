# WebNEI Exporter 架构

## 核心原则

新 exporter 的核心原则是：

```text
NEI final result first
```

也就是说，配方页上实际显示的 visual facts 应优先来自 NEI handler 的最终结果。

visual facts 包括：

- category 来源
- recipe 页内顺序
- input / output / other stack 的可见槽位
- `PositionedStack.items` 的全量候选
- slot 坐标
- handler canvas / tab / icon 等展示信息

semantic facts 由 adapter 补充：

- slot role 修正
- recipe metadata
- slot metadata
- filter tags
- 概率、EU/t、duration、tier 等结构化参数

## 分层

### command

Minecraft command adapter。

允许做：

- 解析命令参数
- 调用 workflow
- 把结果写回 chat

不允许做：

- 扫描 NEI handler
- 写文件
- 直接选择 TSV / JSON / PostgreSQL 等格式细节
- 构造业务数据

### workflow

一次用户动作的编排层。

例如 `HandlerScanWorkflow`：

```text
NeiHandlerScanner.scan()
  -> ExportDataset
  -> IBundleWriter.write(...)
```

workflow 可以知道 scanner 和 bundle writer，但不应该包含底层 TSV 行写入细节。

### nei

NEI 领域层。

当前已有：

```text
nei/scan/NeiHandlerScanner
nei/scan/NeiHandlerDescriptor
```

后续建议扩展：

```text
nei/handler/
nei/recipe/
nei/stack/
```

职责：

- 扫描 NEI handler
- 读取 handler identity
- 加载 `arecipes`
- 抽取 `CachedRecipe`
- 抽取 `PositionedStack.items / relx / rely`

### model

格式无关的中间数据模型。

当前通用模型：

```text
ExportDataset
  name
  sections[]

ExportSection
  name
  columns[]
  rows[]

ExportRow
  values[]
```

这套模型先支持 handler scan / slot extraction 这类早期诊断 bundle。后续正式 recipe export 可以继续扩展领域模型，例如：

```text
ExportCategory
ExportRecipe
ExportSlot
ExportCandidate
ExportMetadata
```

但写出层仍然只面对统一模型或由统一模型派生的数据，不直接依赖 NEI scanner。

### bundle

bundle 是统一写出扩展点。

核心接口：

```text
IBundleWriter
```

当前实现：

```text
bundle/tsv/TsvBundleWriter
```

未来可以增加：

```text
bundle/json/JsonBundleWriter
bundle/postgres/PostgresBundleWriter
bundle/mysql/MySqlBundleWriter
```

规则：

- TSV、JSON、PostgreSQL、MySQL 都是 bundle format。
- 不再维护独立诊断输出体系。
- 诊断输出也通过 bundle writer 写出。
- PostgreSQL 只是一个 writer 实现，不是核心架构。

## 当前 handler scan bundle

当前 `/webnei export handlers` 生成：

```text
handler-scan/nei-handlers.tsv
```

section：`nei-handlers`

字段：

```text
registration_index
source_list
stable_handler_key
handler_class
handler_id
overlay_id
recipe_name
recipe_tab_name
resolved_category_id
mod_id
mod_name
icon_stack_id
catalyst_key
loaded_recipe_count
extraction_status
reason
```

## 当前 slot extraction bundle

当前 `/webnei export slots` 生成：

```text
slot-extraction/handlers.tsv
slot-extraction/recipes.tsv
slot-extraction/stacks.tsv
slot-extraction/candidates.tsv
```

抽取路径使用 NEI 标准公开 API：

```text
IRecipeHandler.numRecipes()
IRecipeHandler.getIngredientStacks(recipeIndex)
IRecipeHandler.getResultStack(recipeIndex)
IRecipeHandler.getOtherStacks(recipeIndex)
```

当前目标是全量尝试所有 handler，先观察标准行为下能抽出多少内容，再和人工校对表对比。

## 后续 Phase 3 方向

后续建议继续补充：

```text
nei/stack/PositionedStackExtractor
adapter/IHandlerAdapter
model/ExportRecipe / ExportSlot / ExportCandidate
```

目标数据流：

```text
NEI handler
  -> standard slot extraction / handler-specific loading
  -> PositionedStack
  -> ExportDataset / domain model
  -> IBundleWriter
```

## 旧 exporter 参考范围

旧 exporter 主要作为已有 WebNEI 数据形态的参考：

- schema 字段含义
- PostgreSQL copy 写出方式
- item / fluid / asset id 经验
- metadata / filter tag 设计
