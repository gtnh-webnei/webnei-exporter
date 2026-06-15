package moe.takochan.webnei.exporter.engine.plan;

import java.io.File;

import moe.takochan.webnei.exporter.WebneiExporterMod;
import net.minecraft.client.Minecraft;

import moe.takochan.webnei.exporter.bundle.BundleContext;
import moe.takochan.webnei.exporter.bundle.BundleException;
import moe.takochan.webnei.exporter.bundle.BundleResult;
import moe.takochan.webnei.exporter.bundle.BundleTarget;
import moe.takochan.webnei.exporter.bundle.IBundleWriter;
import moe.takochan.webnei.exporter.engine.ExportExecutionContext;
import moe.takochan.webnei.exporter.engine.job.ExportJobSession;
import moe.takochan.webnei.exporter.engine.job.IExportJobListener;
import moe.takochan.webnei.exporter.engine.task.ExportTaskContext;
import moe.takochan.webnei.exporter.engine.task.ExportTaskException;
import moe.takochan.webnei.exporter.engine.task.IExportTask;
import moe.takochan.webnei.exporter.domain.ExportModelSet;

/**
 * 通用导出流程执行器。
 *
 * <p>
 * executor 只负责执行已经解析好的 plan：创建 task context、按顺序运行 tasks、
 * 汇总领域模型并交给调用方已选择的 bundle writer。它不决定本次有哪些 task、也不决定使用哪种 bundle。
 * task 之间需要共享 ItemStackCatalog、AssetRequestRegistry 等对象时，通过 ExportTaskContext values 传递。
 */
public final class ExportPlanExecutor {

    /** 执行一个导出计划，并在所有 task 完成后统一写出 bundle。 */
    public BundleResult execute(IExportPlan plan, IBundleWriter bundleWriter, ExportExecutionContext executionContext,
                                ExportJobSession session, IExportJobListener listener) {
        ExportTaskContext taskContext = new ExportTaskContext(executionContext);
        for (IExportTask task : plan.tasks()) {
            session.startTask(task);
            listener.onTaskStarted(session.snapshot());
            try {
                task.execute(taskContext);
            } catch (ExportTaskException | RuntimeException e) {
                WebneiExporterMod.LOG.error("Failed to run WebNEI export task", e);
                return BundleResult.failure(bundleWriter.format(), e.getMessage());
            }
            session.finishTask();
            listener.onTaskFinished(session.snapshot());
        }

        try {
            return bundleWriter
                .write(new ExportModelSet(plan.id(), taskContext.models()), defaultTarget(), BundleContext.defaults());
        } catch (BundleException e) {
            WebneiExporterMod.LOG.error("Failed to write WebNEI export bundle", e);
            return BundleResult.failure(bundleWriter.format(), e.getMessage());
        }
    }

    private static BundleTarget defaultTarget() {
        File outputDirectory = new File(Minecraft.getMinecraft().mcDataDir, "webnei-exporter/bundles");
        return BundleTarget.directory(outputDirectory);
    }
}
