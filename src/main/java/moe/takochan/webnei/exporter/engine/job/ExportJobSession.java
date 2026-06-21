package moe.takochan.webnei.exporter.engine.job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import moe.takochan.webnei.exporter.domain.asset.render.AssetRenderProgress;

/**
 * 导出 job 的统一状态源。
 *
 * <p>
 * GUI 读取同一个 session/snapshot。session 持有完整阶段清单（各 task + bundle 写出阶段），按执行顺序
 * 推进当前阶段索引；并实现 {@link AssetRenderProgress}，让渲染阶段把"已渲染/总数"细粒度进度回灌到
 * 同一状态源，供 GUI 实时展示。
 */
public final class ExportJobSession implements AssetRenderProgress {

    private final long jobId;
    private final List<String> phaseLabels;
    private ExportJobState state = ExportJobState.PENDING;
    private int currentPhase = -1;
    private final List<String> outputFiles = new ArrayList<>();
    private String errorMessage = "";
    private int renderDone;
    private int renderTotal;

    public ExportJobSession(long jobId, List<String> phaseLabels) {
        this.jobId = jobId;
        this.phaseLabels = Collections.unmodifiableList(new ArrayList<>(phaseLabels));
    }

    /** 标记 job 开始执行。 */
    public synchronized void start() {
        state = ExportJobState.RUNNING;
    }

    /** 进入下一个阶段（task 或 bundle 写出），并清空上一个阶段的细粒度进度。 */
    public synchronized void startPhase(int index) {
        currentPhase = index;
        renderDone = 0;
        renderTotal = 0;
    }

    /** 渲染阶段细粒度进度回调。 */
    @Override
    public synchronized void onProgress(int done, int total) {
        renderDone = done;
        renderTotal = total;
    }

    /** 记录最终 bundle writer 产生的输出文件。 */
    public synchronized void finishBundle(List<String> outputs) {
        outputFiles.addAll(outputs);
    }

    /** 标记 job 成功结束，所有阶段视为完成。 */
    public synchronized void finish() {
        state = ExportJobState.DONE;
        currentPhase = phaseLabels.size();
    }

    /** 标记 job 失败并记录原因。 */
    public synchronized void fail(String errorMessage) {
        state = ExportJobState.ERROR;
        this.errorMessage = errorMessage == null ? "" : errorMessage;
    }

    public synchronized ExportJobSnapshot snapshot() {
        List<ExportPhaseView> phases = new ArrayList<>(phaseLabels.size());
        for (int i = 0; i < phaseLabels.size(); i++) {
            phases.add(new ExportPhaseView(phaseLabels.get(i), phaseStateAt(i)));
        }
        return new ExportJobSnapshot(
            jobId,
            state,
            phases,
            currentPhase,
            new ArrayList<>(outputFiles),
            errorMessage,
            renderDone,
            renderTotal);
    }

    private ExportPhaseState phaseStateAt(int index) {
        if (index < currentPhase) {
            return ExportPhaseState.DONE;
        }
        if (index == currentPhase) {
            return ExportPhaseState.RUNNING;
        }
        return ExportPhaseState.PENDING;
    }
}
