package moe.takochan.webnei.exporter.client.gui;

import net.minecraft.client.Minecraft;

import moe.takochan.webnei.exporter.domain.asset.render.client.AssetRenderDispatcher;
import moe.takochan.webnei.exporter.engine.ExportRequest;
import moe.takochan.webnei.exporter.engine.job.ExportJobRunner;
import moe.takochan.webnei.exporter.engine.job.ExportJobSession;

/** 在客户端线程打开导出配置和进度界面。 */
public final class ExportGuiLauncher {

    public static void showConfig() {
        AssetRenderDispatcher.INSTANCE.runLater(new Runnable() {

            @Override
            public void run() {
                Minecraft.getMinecraft()
                    .displayGuiScreen(new GuiExportConfig());
            }
        });
    }

    public static void submitAndShowProgress(final ExportRequest request) {
        AssetRenderDispatcher.INSTANCE.runLater(new Runnable() {

            @Override
            public void run() {
                GuiExportProgress gui = new GuiExportProgress();
                ExportJobSession session = ExportJobRunner.defaults()
                    .submit(request, gui);
                gui.bind(session);
                Minecraft.getMinecraft()
                    .displayGuiScreen(gui);
            }
        });
    }

    private ExportGuiLauncher() {}
}
