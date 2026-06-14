package moe.takochan.webnei.exporter.workflow;

import java.io.File;

import net.minecraft.client.Minecraft;

import moe.takochan.webnei.exporter.WebneiExporterMod;
import moe.takochan.webnei.exporter.bundle.BundleContext;
import moe.takochan.webnei.exporter.bundle.BundleException;
import moe.takochan.webnei.exporter.bundle.BundleFormat;
import moe.takochan.webnei.exporter.bundle.BundleResult;
import moe.takochan.webnei.exporter.bundle.BundleTarget;
import moe.takochan.webnei.exporter.bundle.IBundleWriter;
import moe.takochan.webnei.exporter.bundle.tsv.TsvBundleWriter;
import moe.takochan.webnei.exporter.export.ExportExecutionContext;
import moe.takochan.webnei.exporter.export.ExportJobSession;
import moe.takochan.webnei.exporter.export.IExportJobListener;
import moe.takochan.webnei.exporter.model.ExportDataset;
import moe.takochan.webnei.exporter.plan.IExportPlan;
import moe.takochan.webnei.exporter.step.ExportStepContext;
import moe.takochan.webnei.exporter.step.ExportStepException;
import moe.takochan.webnei.exporter.step.IExportStep;

/**
 * 通用导出流程执行器。
 *
 * <p>
 * workflow 只负责执行已经解析好的 plan：创建 step context、按顺序运行 steps、
 * 汇总 sections 并交给 bundle writer。它不决定本次有哪些 step，也不包含 NEI、item、fluid、asset 的具体逻辑。
 */
public final class ExportWorkflow {

    private final IBundleWriter bundleWriter;

    public ExportWorkflow() {
        this(new TsvBundleWriter());
    }

    ExportWorkflow(IBundleWriter bundleWriter) {
        this.bundleWriter = bundleWriter;
    }

    /** 执行一个导出计划，并在所有 step 完成后统一写出 bundle。 */
    public BundleResult execute(IExportPlan plan, ExportExecutionContext executionContext, ExportJobSession session,
        IExportJobListener listener) {
        ExportStepContext stepContext = new ExportStepContext(executionContext);
        for (IExportStep step : plan.steps()) {
            session.startStep(step);
            listener.onWorkflowStarted(session.snapshot());
            try {
                step.execute(stepContext);
            } catch (ExportStepException e) {
                WebneiExporterMod.LOG.error("Failed to run WebNEI export step", e);
                return BundleResult.failure(BundleFormat.TSV, e.getMessage());
            } catch (RuntimeException e) {
                WebneiExporterMod.LOG.error("Failed to run WebNEI export step", e);
                return BundleResult.failure(BundleFormat.TSV, e.getMessage());
            }
            session.finishStep();
            listener.onWorkflowFinished(
                session.snapshot(),
                BundleResult.success(bundleWriter.format(), session.snapshot().outputFiles));
        }

        try {
            return bundleWriter.write(
                new ExportDataset(stepContext.datasetName(plan.id()), stepContext.sections()),
                defaultTarget(),
                BundleContext.defaults());
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
