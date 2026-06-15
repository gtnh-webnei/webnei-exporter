package moe.takochan.webnei.exporter.engine.task;

/**
 * 单个数据域导出任务。
 *
 * <p>
 * task 才负责具体数据来源，例如 NEI、mod registry、渲染器等。
 * executor 只按 plan 顺序执行 task，不了解 task 内部细节。
 */
public interface IExportTask {

    /** task ID，用于进度显示和诊断。 */
    String id();

    /** task 展示文案 key，用于 chat / future GUI。 */
    String labelKey();

    /** 执行当前数据域导出，并把领域中间模型写入 context。 */
    void execute(ExportTaskContext context) throws ExportTaskException;
}
