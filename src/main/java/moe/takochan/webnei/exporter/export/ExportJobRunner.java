package moe.takochan.webnei.exporter.export;

import java.util.concurrent.atomic.AtomicLong;

import moe.takochan.webnei.exporter.WebneiExporterMod;
import moe.takochan.webnei.exporter.bundle.BundleResult;
import moe.takochan.webnei.exporter.plan.ExportPlanRegistry;
import moe.takochan.webnei.exporter.plan.IExportPlan;
import moe.takochan.webnei.exporter.workflow.ExportWorkflow;

/**
 * 异步导出 job 入口。
 *
 * <p>runner 负责把 request 解析成 plan，并把 plan 交给 workflow 执行。
 * 它不选择 step，也不关心 step 内部如何导出数据。
 */
public final class ExportJobRunner {

    private static final AtomicLong NEXT_JOB_ID = new AtomicLong(1L);

    private final ExportPlanRegistry planRegistry;
    private final ExportWorkflow workflow;

    public ExportJobRunner(ExportPlanRegistry planRegistry, ExportWorkflow workflow) {
        this.planRegistry = planRegistry;
        this.workflow = workflow;
    }

    public static ExportJobRunner defaults() {
        return new ExportJobRunner(ExportPlanRegistry.defaults(), new ExportWorkflow());
    }

    /** 创建 session 并在后台线程执行导出。 */
    public ExportJobSession submit(final ExportRequest request, final IExportJobListener listener) {
        final IExportPlan plan = planRegistry.get(request.planId());
        final ExportJobSession session = new ExportJobSession(
            NEXT_JOB_ID.getAndIncrement(),
            plan == null ? 1 : plan.steps()
                .size());
        session.start();
        listener.onStarted(session.snapshot());

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                runJob(request, plan, session, listener);
            }
        }, "WebNEI Exporter");
        thread.setDaemon(true);
        thread.start();
        return session;
    }

    private void runJob(ExportRequest request, IExportPlan plan, ExportJobSession session, IExportJobListener listener) {
        try {
            if (plan == null) {
                fail(session, listener, "unknown export plan: " + request.planId());
                return;
            }

            ExportExecutionContext context = new ExportExecutionContext(session);
            BundleResult result = workflow.execute(plan, context, session, listener);
            if (!result.success) {
                fail(session, listener, result.errorMessage);
                return;
            }

            session.finishWorkflow(result.outputFiles);
            session.finish();
            listener.onFinished(session.snapshot());
        } catch (Throwable t) {
            WebneiExporterMod.LOG.error("Failed to run WebNEI export job", t);
            fail(
                session,
                listener,
                t.getClass()
                    .getSimpleName() + (t.getMessage() == null ? "" : ": " + t.getMessage()));
        }
    }

    private static void fail(ExportJobSession session, IExportJobListener listener, String reason) {
        session.fail(reason);
        listener.onFailed(session.snapshot());
    }
}
