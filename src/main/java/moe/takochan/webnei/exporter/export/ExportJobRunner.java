package moe.takochan.webnei.exporter.export;

import java.util.concurrent.atomic.AtomicLong;

import moe.takochan.webnei.exporter.WebneiExporterMod;
import moe.takochan.webnei.exporter.bundle.BundleResult;

public final class ExportJobRunner {

    private static final AtomicLong NEXT_JOB_ID = new AtomicLong(1L);

    private final ExportWorkflowRegistry registry;

    public ExportJobRunner(ExportWorkflowRegistry registry) {
        this.registry = registry;
    }

    public static ExportJobRunner defaults() {
        return new ExportJobRunner(ExportWorkflowRegistry.defaults());
    }

    public ExportJobSession submit(final ExportRequest request, final IExportJobListener listener) {
        final ExportJobSession session = new ExportJobSession(
            NEXT_JOB_ID.getAndIncrement(),
            request.workflowIds()
                .size());
        session.start();
        listener.onStarted(session.snapshot());

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                runJob(request, session, listener);
            }
        }, "WebNEI Exporter");
        thread.setDaemon(true);
        thread.start();
        return session;
    }

    private void runJob(ExportRequest request, ExportJobSession session, IExportJobListener listener) {
        try {
            ExportExecutionContext context = new ExportExecutionContext(session);
            for (String workflowId : request.workflowIds()) {
                IExportWorkflow workflow = registry.get(workflowId);
                if (workflow == null) {
                    fail(session, listener, "unknown workflow: " + workflowId);
                    return;
                }

                session.startWorkflow(workflow);
                listener.onWorkflowStarted(session.snapshot());

                BundleResult result = workflow.execute(context);
                if (!result.success) {
                    fail(session, listener, result.errorMessage);
                    return;
                }

                session.finishWorkflow(result.outputFiles);
                listener.onWorkflowFinished(session.snapshot(), result);
            }
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
