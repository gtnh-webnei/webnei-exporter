package moe.takochan.webnei.exporter.step;

/**
 * 单个数据域导出步骤。
 *
 * <p>step 才负责具体数据来源，例如 NEI、mod registry、渲染器等。
 * workflow 只按 plan 顺序执行 step，不了解 step 内部细节。
 */
public interface IExportStep {

    /** step ID，用于进度显示和诊断。 */
    String id();

    /** step 展示文案 key，用于 chat / future GUI。 */
    String labelKey();

    /** 执行当前数据域导出，并把 section 或中间结果写入 context。 */
    void execute(ExportStepContext context) throws ExportStepException;
}
