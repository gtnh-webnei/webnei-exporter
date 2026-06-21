package moe.takochan.webnei.exporter.client.gui;

import java.io.File;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

import moe.takochan.webnei.exporter.engine.job.ExportJobSession;
import moe.takochan.webnei.exporter.engine.job.ExportJobSnapshot;
import moe.takochan.webnei.exporter.engine.job.ExportJobState;
import moe.takochan.webnei.exporter.engine.job.ExportPhaseState;
import moe.takochan.webnei.exporter.engine.job.ExportPhaseView;
import moe.takochan.webnei.exporter.engine.job.IExportJobListener;

/**
 * 导出进度 GUI。
 *
 * <p>
 * 每帧轮询 {@link ExportJobSession} 的快照，按阶段清单实时展示各阶段状态和当前阶段的细粒度进度
 * （如资产渲染 X/N）。同时实现 {@link IExportJobListener}，在尚未绑定 session 时以最近一次事件快照兜底。
 * 导出在后台线程进行，关闭本界面不影响导出。
 */
public final class GuiExportProgress extends GuiScreen implements IExportJobListener {

    private static final int PANEL_WIDTH = 280;
    private static final int BAR_HEIGHT = 10;
    private static final int LINE_HEIGHT = 12;
    private static final int CLOSE_BUTTON_ID = 0;
    private static final int ESC_KEY_CODE = 1;

    private static final int COLOR_TITLE = 0xFFFFFF;
    private static final int COLOR_DONE = 0x66BB6A;
    private static final int COLOR_RUNNING = 0xFFD54F;
    private static final int COLOR_PENDING = 0x808080;
    private static final int COLOR_SUBTEXT = 0xA0A0A0;
    private static final int COLOR_ERROR = 0xFF5555;
    private static final int COLOR_BAR_BG = 0xFF202020;
    private static final int COLOR_BAR_FILL = 0xFF4CAF50;
    private static final int COLOR_BAR_BORDER = 0xFF000000;

    private static final String MARK_DONE = "[x] ";
    private static final String MARK_RUNNING = "[>] ";
    private static final String MARK_PENDING = "[ ] ";

    private volatile ExportJobSession session;
    private volatile ExportJobSnapshot latest;
    private GuiButton closeButton;

    /** 绑定实时状态源，提交导出后由命令侧调用。 */
    public void bind(ExportJobSession session) {
        this.session = session;
    }

    @Override
    public void initGui() {
        int x = (width - 100) / 2;
        int y = height - 40;
        buttonList.clear();
        closeButton = new GuiButton(CLOSE_BUTTON_ID, x, y, 100, 20, label("webnei.gui.export.close"));
        closeButton.visible = false;
        buttonList.add(closeButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == CLOSE_BUTTON_ID) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        // 仅在导出结束后才允许 ESC 关闭，运行中屏蔽以免误关。
        if (keyCode == ESC_KEY_CODE && !isFinished(currentSnapshot())) {
            return;
        }
        super.keyTyped(typedChar, keyCode);
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
        int left = centerX - PANEL_WIDTH / 2;
        int y = 40;

        drawCenteredString(fontRendererObj, label("webnei.gui.export.title"), centerX, y, COLOR_TITLE);
        y += 14;
        drawCenteredString(fontRendererObj, stateText(snapshot), centerX, y, COLOR_SUBTEXT);
        y += 20;

        if (snapshot != null) {
            y = drawPhases(snapshot, left, y);
            drawFooter(snapshot, left, y + 4);
        }
        if (closeButton != null) {
            closeButton.visible = isFinished(snapshot);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private static boolean isFinished(ExportJobSnapshot snapshot) {
        return snapshot != null
            && (snapshot.getState() == ExportJobState.DONE || snapshot.getState() == ExportJobState.ERROR);
    }

    private int drawPhases(ExportJobSnapshot snapshot, int left, int top) {
        int y = top;
        for (ExportPhaseView phase : snapshot.getPhases()) {
            ExportPhaseState phaseState = phase.getState();
            fontRendererObj.drawString(mark(phaseState) + label(phase.getLabelKey()), left, y, phaseColor(phaseState));
            y += LINE_HEIGHT;
            if (phaseState == ExportPhaseState.RUNNING && snapshot.getRenderTotal() > 0) {
                y = drawRenderProgress(snapshot, left + 16, y);
            }
        }
        return y;
    }

    private int drawRenderProgress(ExportJobSnapshot snapshot, int left, int top) {
        int total = snapshot.getRenderTotal();
        int done = Math.min(snapshot.getRenderDone(), total);
        fontRendererObj.drawString(
            StatCollector.translateToLocalFormatted(
                "webnei.gui.export.rendering",
                Integer.toString(done),
                Integer.toString(total)),
            left,
            top,
            COLOR_SUBTEXT);
        int y = top + LINE_HEIGHT;
        drawBar(left, y, PANEL_WIDTH - 16, done / (float) total);
        return y + BAR_HEIGHT + 4;
    }

    private void drawFooter(ExportJobSnapshot snapshot, int left, int top) {
        if (snapshot.getState() == ExportJobState.ERROR) {
            fontRendererObj.drawSplitString(
                StatCollector
                    .translateToLocalFormatted("webnei.command.export.failed", safe(snapshot.getErrorMessage())),
                left,
                top,
                PANEL_WIDTH,
                COLOR_ERROR);
            return;
        }
        if (snapshot.getState() == ExportJobState.DONE) {
            String dir = outputDirectory(snapshot);
            if (dir.isEmpty()) {
                return;
            }
            fontRendererObj.drawString(label("webnei.gui.export.outputDir"), left, top, COLOR_TITLE);
            int y = top + LINE_HEIGHT;
            for (Object line : fontRendererObj.listFormattedStringToWidth(dir, PANEL_WIDTH)) {
                fontRendererObj.drawString(line.toString(), left, y, COLOR_SUBTEXT);
                y += LINE_HEIGHT - 1;
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

    private static String outputDirectory(ExportJobSnapshot snapshot) {
        if (snapshot.getOutputFiles()
            .isEmpty()) {
            return "";
        }
        File parent = new File(
            snapshot.getOutputFiles()
                .get(0)).getParentFile();
        return parent == null ? "" : parent.getAbsolutePath();
    }

    private static String mark(ExportPhaseState state) {
        switch (state) {
            case DONE:
                return MARK_DONE;
            case RUNNING:
                return MARK_RUNNING;
            default:
                return MARK_PENDING;
        }
    }

    private static int phaseColor(ExportPhaseState state) {
        switch (state) {
            case DONE:
                return COLOR_DONE;
            case RUNNING:
                return COLOR_RUNNING;
            default:
                return COLOR_PENDING;
        }
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
