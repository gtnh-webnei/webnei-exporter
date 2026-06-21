package moe.takochan.webnei.exporter.engine.job;

/** 单个导出阶段的只读视图，供 GUI 展示阶段清单。 */
public final class ExportPhaseView {

    private final String labelKey;
    private final ExportPhaseState state;

    public ExportPhaseView(String labelKey, ExportPhaseState state) {
        this.labelKey = labelKey;
        this.state = state;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public ExportPhaseState getState() {
        return state;
    }
}
