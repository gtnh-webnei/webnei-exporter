package moe.takochan.webnei.exporter.engine;

import moe.takochan.webnei.exporter.engine.job.ExportJobSession;
import moe.takochan.webnei.exporter.engine.job.ExportJobSnapshot;

/** job 执行期间传给 plan executor 和 task 的请求与状态上下文。 */
public final class ExportExecutionContext {

    /** 本次导出的原始请求。 */
    private final ExportRequest request;

    /** 本次导出的 session 状态源。 */
    private final ExportJobSession session;

    public ExportExecutionContext(ExportRequest request, ExportJobSession session) {
        this.request = request;
        this.session = session;
    }

    /** 返回本次导出的原始请求。 */
    public ExportRequest request() {
        return request;
    }

    /** 返回当前 job 状态快照。 */
    public ExportJobSnapshot snapshot() {
        return session.snapshot();
    }
}
