package moe.takochan.webnei.exporter.step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moe.takochan.webnei.exporter.export.ExportExecutionContext;
import moe.takochan.webnei.exporter.model.ExportSection;

/**
 * step 之间共享的导出上下文。
 *
 * <p>它负责两件事：
 * <ol>
 *   <li>收集 step 输出的 bundle sections。</li>
 *   <li>保存同一次 plan 内的临时中间结果，例如 handler 扫描结果。</li>
 * </ol>
 * 这里不放 TSV/JSON/SQL 写出逻辑，最终格式仍由 bundle writer 决定。
 */
public final class ExportStepContext {

    private final ExportExecutionContext executionContext;
    private final List<ExportSection> sections = new ArrayList<>();
    private final Map<String, Object> values = new HashMap<>();

    public ExportStepContext(ExportExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    /** job/session 级上下文，供 step 读取当前导出状态。 */
    public ExportExecutionContext executionContext() {
        return executionContext;
    }

    /** 添加一个待写出的 section，由 workflow 最后统一交给 bundle writer。 */
    public void addSection(ExportSection section) {
        sections.add(section);
    }

    /** 返回当前已收集的 sections 副本，避免外部直接修改内部列表。 */
    public List<ExportSection> sections() {
        return new ArrayList<>(sections);
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
