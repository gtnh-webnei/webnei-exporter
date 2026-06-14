package moe.takochan.webnei.exporter.export;

public final class ExportExecutionContext {

    private final ExportRequest request;
    private final ExportJobSession session;

    ExportExecutionContext(ExportRequest request, ExportJobSession session) {
        this.request = request;
        this.session = session;
    }

    public ExportRequest request() {
        return request;
    }

    public ExportJobSnapshot snapshot() {
        return session.snapshot();
    }
}
