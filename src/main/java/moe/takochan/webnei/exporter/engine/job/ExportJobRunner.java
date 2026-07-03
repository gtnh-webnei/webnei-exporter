package moe.takochan.webnei.exporter.engine.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import moe.takochan.webnei.exporter.WebneiExporterMod;
import moe.takochan.webnei.exporter.bundle.BundleException;
import moe.takochan.webnei.exporter.bundle.BundleFormat;
import moe.takochan.webnei.exporter.bundle.BundleResult;
import moe.takochan.webnei.exporter.bundle.BundleWriterRegistry;
import moe.takochan.webnei.exporter.bundle.IBundleWriter;
import moe.takochan.webnei.exporter.bundle.MultiBundleWriter;
import moe.takochan.webnei.exporter.engine.ExportExecutionContext;
import moe.takochan.webnei.exporter.engine.ExportRequest;
import moe.takochan.webnei.exporter.engine.plan.ExportPlanExecutor;
import moe.takochan.webnei.exporter.engine.plan.IExportPlan;
import moe.takochan.webnei.exporter.engine.store.DomainStoreRegistry;
import moe.takochan.webnei.exporter.engine.task.IExportTask;
import moe.takochan.webnei.exporter.export.ExportPlanRegistry;

/**
 * 异步导出 job 入口。
 *
 * <p>
 * runner 负责把 request 解析成 plan 和 bundle writer，并把它们交给 executor 执行。
 * 它不选择 task，也不关心 task 内部如何导出数据。
 */
public final class ExportJobRunner {

    private static final AtomicLong NEXT_JOB_ID = new AtomicLong(1L);

    private final ExportPlanRegistry planRegistry;
    private final BundleWriterRegistry bundleWriterRegistry;
    private final ExportPlanExecutor executor;

    /** 创建可注入 plan registry、bundle writer registry 和 executor 的 runner。 */
    public ExportJobRunner(ExportPlanRegistry planRegistry, BundleWriterRegistry bundleWriterRegistry,
        ExportPlanExecutor executor) {
        this.planRegistry = planRegistry;
        this.bundleWriterRegistry = bundleWriterRegistry;
        this.executor = executor;
    }

    /** 使用当前默认 plan、bundle writer 和 executor 创建 runner。 */
    public static ExportJobRunner defaults() {
        return new ExportJobRunner(
            ExportPlanRegistry.defaults(),
            BundleWriterRegistry.defaults(),
            new ExportPlanExecutor());
    }

    /** bundle 写出阶段的展示文案 key；该阶段包含资产渲染等耗时工作。 */
    public static final String BUNDLE_PHASE_LABEL_KEY = "webnei.task.bundle";

    /** 创建 session 并在后台线程执行导出。 */
    public ExportJobSession submit(final ExportRequest request, final IExportJobListener listener) {
        final IExportPlan plan = planRegistry.get(request.planId());
        final ExportJobSession session = new ExportJobSession(NEXT_JOB_ID.getAndIncrement(), phaseLabels(plan));
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

    private static List<String> phaseLabels(IExportPlan plan) {
        List<String> labels = new ArrayList<>();
        if (plan != null) {
            for (IExportTask task : plan.tasks()) {
                labels.add(task.labelKey());
            }
        }
        labels.add(BUNDLE_PHASE_LABEL_KEY);
        return labels;
    }

    private void runJob(ExportRequest request, IExportPlan plan, ExportJobSession session,
        IExportJobListener listener) {
        try {
            if (plan == null) {
                fail(session, listener, "unknown export plan: " + request.planId());
                return;
            }

            IBundleWriter bundleWriter = bundleWriter(request);
            ExportExecutionContext context = new ExportExecutionContext(request, session);
            DomainStoreRegistry storeRegistry = new DomainStoreRegistry();
            BundleResult result = executor.execute(plan, bundleWriter, context, storeRegistry, session, listener);
            if (!result.isSuccess()) {
                fail(session, listener, result.getErrorMessage());
                return;
            }

            session.finishBundle(result.getOutputFiles());
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

    private IBundleWriter bundleWriter(ExportRequest request) throws BundleException {
        List<BundleFormat> formats = request.bundleFormats();
        if (formats.size() == 1) {
            return bundleWriterRegistry.writerFor(formats.get(0));
        }
        List<IBundleWriter> writers = new ArrayList<>();
        for (BundleFormat format : formats) {
            writers.add(bundleWriterRegistry.writerFor(format));
        }
        return new MultiBundleWriter(writers);
    }

    private static void fail(ExportJobSession session, IExportJobListener listener, String reason) {
        session.fail(reason);
        listener.onFailed(session.snapshot());
    }
}
