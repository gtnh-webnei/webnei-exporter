package moe.takochan.webnei.exporter.engine.job;

import java.util.Collections;
import java.util.List;

import lombok.Getter;

/** 导出 job 的只读状态快照，供 GUI 展示进度。 */
@Getter
public final class ExportJobSnapshot {

    /** 本 JVM 内递增的 job ID。 */
    private final long jobId;

    /** 当前 job 状态。 */
    private final ExportJobState state;

    /** 完整阶段清单（各 task + bundle 写出阶段），按执行顺序。 */
    private final List<ExportPhaseView> phases;

    /** 当前正在执行的阶段索引；-1 表示尚未开始，等于 phases.size() 表示全部完成。 */
    private final int currentPhase;

    /** bundle writer 最终产生的输出文件路径。 */
    private final List<String> outputFiles;

    /** 失败原因；非失败状态下为空。 */
    private final String errorMessage;

    /** 当前阶段已完成的细粒度单元数（如已渲染图标数）。 */
    private final int renderDone;

    /** 当前阶段细粒度单元总数（如待渲染图标总数）；为 0 表示当前阶段无细粒度进度。 */
    private final int renderTotal;

    public ExportJobSnapshot(long jobId, ExportJobState state, List<ExportPhaseView> phases, int currentPhase,
        List<String> outputFiles, String errorMessage, int renderDone, int renderTotal) {
        this.jobId = jobId;
        this.state = state;
        this.phases = Collections.unmodifiableList(phases);
        this.currentPhase = currentPhase;
        this.outputFiles = Collections.unmodifiableList(outputFiles);
        this.errorMessage = errorMessage;
        this.renderDone = renderDone;
        this.renderTotal = renderTotal;
    }
}
