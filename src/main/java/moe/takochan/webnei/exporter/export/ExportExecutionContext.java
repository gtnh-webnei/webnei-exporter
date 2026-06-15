package moe.takochan.webnei.exporter.export;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/** job 执行期间传给 workflow/step 的请求和状态上下文。 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ExportExecutionContext {

    /** 本次导出的原始请求。 */
    private final ExportRequest request;

    /** 本次导出的 session 状态源。 */
    private final ExportJobSession session;

    public ExportRequest request() {
        return request;
    }

    public ExportJobSnapshot snapshot() {
        return session.snapshot();
    }
}
