# WebNEI Exporter

WebNEI Exporter 是新的 GTNH / NEI 数据导出 mod。它以 NEI handler 的最终展示结果为主要事实来源，导出 WebNEI 需要的数据。

## 设计目标

- 以 NEI final result 作为 visual facts 权威来源。
- 通用抽取层负责扫描 handler、读取 recipe、抽取 `PositionedStack`、保留候选列表和坐标。
- handler adapter 只补充语义信息，例如 slot role、metadata、filter tags、概率、机器参数等。
- bundle 写出是可插拔能力，同一个中间模型可以写成 TSV、JSON、PostgreSQL、MySQL 等不同格式。
- 诊断输出也走 bundle 写出机制，不维护另一套输出体系。

## 当前命令

```text
/webnei help
/webnei export help
/webnei export handlers
/webnei export slots
```

命令支持 tab 补全，并通过 `assets/webnei/lang` 提供 i18n 文案。

## 当前 bundle

`handlers` 会扫描 NEI 已注册 handler：

```text
<mcDataDir>/webnei-exporter/bundles/handler-scan/nei-handlers.tsv
```

`slots` 会对所有 handler 尝试执行标准 NEI slot 抽取：

```text
<mcDataDir>/webnei-exporter/bundles/slot-extraction/handlers.tsv
<mcDataDir>/webnei-exporter/bundles/slot-extraction/recipes.tsv
<mcDataDir>/webnei-exporter/bundles/slot-extraction/stacks.tsv
<mcDataDir>/webnei-exporter/bundles/slot-extraction/candidates.tsv
```

## 当前包结构

```text
moe.takochan.webnei.exporter
├── command/      # Minecraft command adapter，只负责命令分发、tab 补全和 chat 反馈
├── workflow/     # 用户动作编排，例如 handler scan / slot extraction workflow
├── nei/scan/     # NEI handler 扫描与 handler 领域描述
├── nei/recipe/   # 标准 NEI recipe/slot/candidate 抽取
├── model/        # 格式无关的中间数据集模型
└── bundle/       # bundle 写出抽象和具体 writer 实现
```

## 数据流

```text
/webnei export handlers
  -> command.HandlerScanCommand
  -> workflow.HandlerScanWorkflow
  -> nei.scan.NeiHandlerScanner
  -> model.ExportDataset
  -> bundle.IBundleWriter
  -> bundle.tsv.TsvBundleWriter
```

```text
/webnei export slots
  -> command.SlotExtractionCommand
  -> workflow.SlotExtractionWorkflow
  -> nei.scan.NeiHandlerScanner
  -> nei.recipe.StandardSlotExtractor
  -> model.ExportDataset
  -> bundle.IBundleWriter
  -> bundle.tsv.TsvBundleWriter
```

## 构建

```bash
./gradlew compileJava
```

需要完整产物时再运行：

```bash
./gradlew build
```

## 约束

- NEI 展示事实优先从 NEI final result 抽取。
- PostgreSQL 写出只是 bundle writer 的一种实现。
- 不新增独立诊断输出体系；诊断输出也走 bundle writer。
- `command/` 不直接读取 NEI、不写文件、不依赖具体 bundle 格式。
