package moe.takochan.webnei.exporter.engine.job;

import java.util.ArrayList;
import java.util.Arrays;
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
    private long startNanos = -1L;
    private long endNanos = -1L;
    private long phaseStartNanos = -1L;
    private final long[] phaseElapsedNanos;

    public ExportJobSession(long jobId, List<String> phaseLabels) {
        this.jobId = jobId;
        this.phaseLabels = Collections.unmodifiableList(new ArrayList<>(phaseLabels));
        this.phaseElapsedNanos = new long[this.phaseLabels.size()];
        Arrays.fill(this.phaseElapsedNanos, -1L);
    }

    /** 标记 job 开始执行。 */
    public synchronized void start() {
        state = ExportJobState.RUNNING;
        startNanos = System.nanoTime();
    }

    /** 进入下一个阶段（task 或 bundle 写出）：结算上一阶段耗时、清空细粒度进度、记录本阶段起点。 */
    public synchronized void startPhase(int index) {
        closeCurrentPhase();
        currentPhase = index;
        phaseStartNanos = System.nanoTime();
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
        closeCurrentPhase();
        state = ExportJobState.DONE;
        currentPhase = phaseLabels.size();
        endNanos = System.nanoTime();
    }

    /** 标记 job 失败并记录原因。 */
    public synchronized void fail(String errorMessage) {
        closeCurrentPhase();
        state = ExportJobState.ERROR;
        this.errorMessage = errorMessage == null ? "" : errorMessage;
        endNanos = System.nanoTime();
    }

    /** 结算当前阶段已用时；阶段切换、结束、失败时调用。 */
    private void closeCurrentPhase() {
        if (currentPhase >= 0 && currentPhase < phaseElapsedNanos.length && phaseStartNanos >= 0L) {
            phaseElapsedNanos[currentPhase] = System.nanoTime() - phaseStartNanos;
        }
    }

    public synchronized ExportJobSnapshot snapshot() {
        long now = System.nanoTime();
        List<ExportPhaseView> phases = new ArrayList<>(phaseLabels.size());
        for (int i = 0; i < phaseLabels.size(); i++) {
            phases.add(new ExportPhaseView(phaseLabels.get(i), phaseStateAt(i), phaseElapsedMillis(i, now)));
        }
        return new ExportJobSnapshot(
            jobId,
            state,
            phases,
            currentPhase,
            new ArrayList<>(outputFiles),
            errorMessage,
            renderDone,
            renderTotal,
            totalElapsedMillis(now));
    }

    /** 阶段耗时（毫秒）：已结算阶段用记录值，当前运行阶段用实时值，未开始为 -1。 */
    private long phaseElapsedMillis(int index, long now) {
        if (phaseElapsedNanos[index] >= 0L) {
            return phaseElapsedNanos[index] / 1_000_000L;
        }
        if (index == currentPhase && phaseStartNanos >= 0L) {
            return (now - phaseStartNanos) / 1_000_000L;
        }
        return -1L;
    }

    /** 总耗时（毫秒）：结束后用 end，运行中用实时值，未开始为 -1。 */
    private long totalElapsedMillis(long now) {
        if (startNanos < 0L) {
            return -1L;
        }
        long until = endNanos >= 0L ? endNanos : now;
        return (until - startNanos) / 1_000_000L;
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
