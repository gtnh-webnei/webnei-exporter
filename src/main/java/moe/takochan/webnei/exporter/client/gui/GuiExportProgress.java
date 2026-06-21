package moe.takochan.webnei.exporter.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

import moe.takochan.webnei.exporter.engine.job.ExportJobSession;
import moe.takochan.webnei.exporter.engine.job.ExportJobSnapshot;
import moe.takochan.webnei.exporter.engine.job.ExportJobState;
import moe.takochan.webnei.exporter.engine.job.IExportJobListener;

/**
 * 导出进度 GUI。
 *
 * <p>
 * 每帧轮询 {@link ExportJobSession} 的快照实时展示阶段与渲染进度。同时实现 {@link IExportJobListener}，
 * 在尚未绑定 session 时以最近一次事件快照兜底。导出在后台线程进行，关闭本界面不影响导出。
 */
public final class GuiExportProgress extends GuiScreen implements IExportJobListener {

    private static final int PANEL_WIDTH = 280;
    private static final int BAR_HEIGHT = 12;
    private static final int CLOSE_BUTTON_ID = 0;

    private static final int COLOR_TEXT = 0xFFFFFF;
    private static final int COLOR_SUBTEXT = 0xA0A0A0;
    private static final int COLOR_ERROR = 0xFF5555;
    private static final int COLOR_BAR_BG = 0xFF202020;
    private static final int COLOR_BAR_FILL = 0xFF4CAF50;
    private static final int COLOR_BAR_BORDER = 0xFF000000;

    private volatile ExportJobSession session;
    private volatile ExportJobSnapshot latest;

    /** 绑定实时状态源，提交导出后由命令侧调用。 */
    public void bind(ExportJobSession session) {
        this.session = session;
    }

    @Override
    public void initGui() {
        int x = (width - 100) / 2;
        int y = height / 2 + 60;
        buttonList.clear();
        buttonList.add(new GuiButton(CLOSE_BUTTON_ID, x, y, 100, 20, label("webnei.gui.export.close")));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == CLOSE_BUTTON_ID) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        ExportJobSnapshot snapshot = currentSnapshot();

        int centerX = width / 2;
        int top = height / 2 - 60;

        drawCenteredString(fontRendererObj, label("webnei.gui.export.title"), centerX, top, COLOR_TEXT);
        drawCenteredString(fontRendererObj, stateText(snapshot), centerX, top + 16, COLOR_SUBTEXT);

        if (snapshot != null) {
            drawBody(snapshot, centerX, top);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    // BODY_MARKER
    private void drawBody(ExportJobSnapshot snapshot, int centerX, int top) {
        int left = centerX - PANEL_WIDTH / 2;
        int y = top + 38;

        String tasks = StatCollector.translateToLocalFormatted(
            "webnei.gui.export.tasks",
            Integer.toString(snapshot.getCompletedTasks()),
            Integer.toString(snapshot.getTotalTasks()));
        fontRendererObj.drawString(tasks, left, y, COLOR_TEXT);
        y += 14;

        if (snapshot.getState() == ExportJobState.ERROR) {
            fontRendererObj.drawSplitString(
                StatCollector
                    .translateToLocalFormatted("webnei.command.export.failed", safe(snapshot.getErrorMessage())),
                left,
                y,
                PANEL_WIDTH,
                COLOR_ERROR);
            return;
        }

        if (snapshot.getState() == ExportJobState.DONE) {
            drawOutputs(snapshot, left, y);
            return;
        }

        String phase = phaseText(snapshot);
        if (!phase.isEmpty()) {
            fontRendererObj.drawString(
                StatCollector.translateToLocalFormatted("webnei.gui.export.phase", phase),
                left,
                y,
                COLOR_SUBTEXT);
            y += 14;
        }

        int total = snapshot.getRenderTotal();
        if (total > 0) {
            int done = Math.min(snapshot.getRenderDone(), total);
            fontRendererObj.drawString(
                StatCollector.translateToLocalFormatted(
                    "webnei.gui.export.rendering",
                    Integer.toString(done),
                    Integer.toString(total)),
                left,
                y,
                COLOR_TEXT);
            y += 14;
            drawBar(left, y, PANEL_WIDTH, done / (float) total);
        }
    }

    private void drawOutputs(ExportJobSnapshot snapshot, int left, int y) {
        fontRendererObj.drawString(label("webnei.gui.export.outputs"), left, y, COLOR_TEXT);
        y += 14;
        for (String file : snapshot.getOutputFiles()) {
            for (Object line : fontRendererObj.listFormattedStringToWidth(file, PANEL_WIDTH)) {
                fontRendererObj.drawString(line.toString(), left, y, COLOR_SUBTEXT);
                y += 11;
            }
        }
    }

    private void drawBar(int left, int top, int barWidth, float fraction) {
        float clamped = Math.max(0.0F, Math.min(1.0F, fraction));
        drawRect(left - 1, top - 1, left + barWidth + 1, top + BAR_HEIGHT + 1, COLOR_BAR_BORDER);
        drawRect(left, top, left + barWidth, top + BAR_HEIGHT, COLOR_BAR_BG);
        drawRect(left, top, left + Math.round(barWidth * clamped), top + BAR_HEIGHT, COLOR_BAR_FILL);
    }

    private ExportJobSnapshot currentSnapshot() {
        ExportJobSession live = session;
        if (live != null) {
            return live.snapshot();
        }
        return latest;
    }

    private static String phaseText(ExportJobSnapshot snapshot) {
        String key = snapshot.getCurrentTaskLabelKey();
        if (key == null || key.isEmpty()) {
            return snapshot.getCurrentTaskId() == null ? "" : snapshot.getCurrentTaskId();
        }
        return StatCollector.translateToLocal(key);
    }

    private static String stateText(ExportJobSnapshot snapshot) {
        if (snapshot == null) {
            return label("webnei.gui.export.state.pending");
        }
        switch (snapshot.getState()) {
            case RUNNING:
                return label("webnei.gui.export.state.running");
            case DONE:
                return label("webnei.gui.export.state.done");
            case ERROR:
                return label("webnei.gui.export.state.error");
            default:
                return label("webnei.gui.export.state.pending");
        }
    }

    private static String label(String key) {
        return StatCollector.translateToLocal(key);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    @Override
    public void onStarted(ExportJobSnapshot snapshot) {
        latest = snapshot;
    }

    @Override
    public void onTaskStarted(ExportJobSnapshot snapshot) {
        latest = snapshot;
    }

    @Override
    public void onTaskFinished(ExportJobSnapshot snapshot) {
        latest = snapshot;
    }

    @Override
    public void onFinished(ExportJobSnapshot snapshot) {
        latest = snapshot;
    }

    @Override
    public void onFailed(ExportJobSnapshot snapshot) {
        latest = snapshot;
    }
}
