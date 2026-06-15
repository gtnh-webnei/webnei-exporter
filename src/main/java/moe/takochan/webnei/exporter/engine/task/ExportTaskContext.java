package moe.takochan.webnei.exporter.engine.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moe.takochan.webnei.exporter.engine.ExportExecutionContext;
import moe.takochan.webnei.exporter.domain.IExportModel;

/**
 * task 之间共享的导出上下文。
 *
 * <p>
 * 它负责两件事：
 * <ol>
 * <li>收集 task 输出的领域中间模型。</li>
 * <li>保存同一次 plan 内的临时中间结果，例如 handler 扫描结果。</li>
 * </ol>
 * 这里不放 TSV/JSON/SQL 写出逻辑，最终格式和输出位置仍由 bundle writer 决定。
 */
public final class ExportTaskContext {

    private final ExportExecutionContext executionContext;
    private final List<IExportModel> models = new ArrayList<>();
    private final Map<String, Object> values = new HashMap<>();

    public ExportTaskContext(ExportExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    /** job/session 级上下文，供 task 读取当前导出状态。 */
    public ExportExecutionContext executionContext() {
        return executionContext;
    }

    /** 添加一个领域中间模型，由 executor 最后统一交给 bundle writer；不要在 task 中添加 TSV section。 */
    public void addModel(IExportModel model) {
        models.add(model);
    }

    /** 返回当前已收集的模型副本，避免外部直接修改内部列表。 */
    public List<IExportModel> models() {
        return new ArrayList<>(models);
    }

    /** 保存 plan 内临时中间结果；key 应由提供方集中定义。 */
    public void put(String key, Object value) {
        values.put(key, value);
    }

    /** 读取 plan 内临时中间结果。 */
    public Object get(String key) {
        return values.get(key);
    }
}
