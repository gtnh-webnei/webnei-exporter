package moe.takochan.webnei.exporter.export;

public final class ExportExecutionContext {

    private final ExportJobSession session;

    ExportExecutionContext(ExportJobSession session) {
        this.session = session;
    }

    public ExportJobSnapshot snapshot() {
        return session.snapshot();
    }
}
