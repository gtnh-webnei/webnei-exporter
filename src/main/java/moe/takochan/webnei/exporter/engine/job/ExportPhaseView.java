package moe.takochan.webnei.exporter.engine.job;

/** 单个导出阶段的只读视图，供 GUI 展示阶段清单。 */
public final class ExportPhaseView {

    private final String labelKey;
    private final ExportPhaseState state;
    private final long elapsedMillis;

    public ExportPhaseView(String labelKey, ExportPhaseState state, long elapsedMillis) {
        this.labelKey = labelKey;
        this.state = state;
        this.elapsedMillis = elapsedMillis;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public ExportPhaseState getState() {
        return state;
    }

    /** 该阶段耗时（毫秒）；尚未开始为 -1，运行中为实时累计值。 */
    public long getElapsedMillis() {
        return elapsedMillis;
    }
}
