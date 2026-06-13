# WebNEI Exporter

WebNEI Exporter 是新的 GTNH / NEI 数据导出 mod。它以 NEI handler 的最终展示结果为主要事实来源，导出 WebNEI 需要的数据。

## 设计目标

- 以 NEI final result 作为 visual facts 权威来源。
- 通用抽取层负责扫描 handler、读取 recipe、抽取 `PositionedStack`、保留候选列表和坐标。
- handler adapter 只补充语义信息，例如 slot role、metadata、filter tags、概率、机器参数等。
- bundle 写出是可插拔能力，同一个中间模型可以写成 TSV、JSON、PostgreSQL、MySQL 等不同格式。
- 不再维护单独的 report 写出体系；诊断输出也是 bundle 的一种格式。

## 当前能力

当前阶段只实现了 handler scan 的最小闭环：

```text
/webnei export handlers
```

该命令会扫描 NEI 已注册 handler，并通过统一 bundle 写出机制输出 TSV bundle：

```text
<mcDataDir>/webnei-exporter/bundles/handler-scan/nei-handlers.tsv
```

## 当前包结构

```text
moe.takochan.webnei.exporter
├── command/      # Minecraft command adapter，只负责命令分发和 chat 反馈
├── workflow/     # 用户动作编排，例如 handler scan workflow
├── nei/scan/     # NEI handler 扫描与 handler 领域描述
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

后续 Phase 3 的 recipe / slot 抽取也应走同一条主线：先生成中间模型，再交给 bundle writer 写出。

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
- 不新增独立 report writer；诊断输出也走 bundle writer。
- `command/` 不直接读取 NEI、不写文件、不依赖具体 bundle 格式。
