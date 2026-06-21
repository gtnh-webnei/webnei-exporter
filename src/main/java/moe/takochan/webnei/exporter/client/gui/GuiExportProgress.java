package moe.takochan.webnei.exporter.client.gui;

import java.io.File;
import java.util.List;

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
    private static final int HEADER_TOP = 24;
    private static final int LIST_TOP = 56;
    private static final int FOOTER_HEIGHT = 28;
    private static final int BUTTON_MARGIN = 32;
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

        drawCenteredString(fontRendererObj, label("webnei.gui.export.title"), centerX, HEADER_TOP, COLOR_TITLE);
        drawCenteredString(fontRendererObj, stateText(snapshot), centerX, HEADER_TOP + 14, COLOR_SUBTEXT);

        if (snapshot != null) {
            int footerTop = height - FOOTER_HEIGHT - BUTTON_MARGIN;
            drawPhaseList(snapshot, left, LIST_TOP, footerTop - 4);
            drawFooter(snapshot, left, footerTop);
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

    /**
     * 在 [top, bottom) 的有界区域内绘制阶段清单，超出时按当前阶段自动滚动，并在上下方提示被折叠的阶段数。
     */
    private void drawPhaseList(ExportJobSnapshot snapshot, int left, int top, int bottom) {
        List<ExportPhaseView> phases = snapshot.getPhases();
        int total = phases.size();
        if (total == 0) {
            return;
        }
        int rows = Math.max(1, (bottom - top) / LINE_HEIGHT);
        int start = scrollStart(phases, total, rows);
        int end = Math.min(total, start + rows);

        int y = top;
        if (start > 0) {
            fontRendererObj.drawString(moreAbove(start), left, y, COLOR_PENDING);
            y += LINE_HEIGHT;
        }
        int bodyEnd = (end < total) ? end - 1 : end;
        for (int i = start; i < bodyEnd; i++) {
            drawPhaseRow(phases.get(i), left, y);
            y += LINE_HEIGHT;
        }
        if (end < total) {
            fontRendererObj.drawString(moreBelow(total - bodyEnd), left, y, COLOR_PENDING);
        }
    }

    private void drawPhaseRow(ExportPhaseView phase, int left, int y) {
        ExportPhaseState phaseState = phase.getState();
        fontRendererObj.drawString(mark(phaseState) + label(phase.getLabelKey()), left, y, phaseColor(phaseState));
    }

    /** 计算首个可见阶段索引：尽量让当前 RUNNING 阶段保持在可视区内。 */
    private static int scrollStart(List<ExportPhaseView> phases, int total, int rows) {
        if (total <= rows) {
            return 0;
        }
        int running = 0;
        for (int i = 0; i < total; i++) {
            if (phases.get(i)
                .getState() == ExportPhaseState.RUNNING) {
                running = i;
                break;
            }
        }
        int start = running - rows / 2;
        if (start < 0) {
            start = 0;
        }
        if (start > total - rows) {
            start = total - rows;
        }
        return start;
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
            return;
        }
        if (snapshot.getRenderTotal() > 0) {
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
            drawBar(left, top + LINE_HEIGHT, PANEL_WIDTH, done / (float) total);
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

    private static String moreAbove(int count) {
        return StatCollector.translateToLocalFormatted("webnei.gui.export.moreAbove", Integer.toString(count));
    }

    private static String moreBelow(int count) {
        return StatCollector.translateToLocalFormatted("webnei.gui.export.moreBelow", Integer.toString(count));
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
