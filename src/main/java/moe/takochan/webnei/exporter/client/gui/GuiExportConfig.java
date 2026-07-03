package moe.takochan.webnei.exporter.client.gui;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import moe.takochan.webnei.exporter.bundle.BundleFormat;
import moe.takochan.webnei.exporter.engine.ExportRequest;
import moe.takochan.webnei.exporter.engine.ExportRequestOptions;
import moe.takochan.webnei.exporter.export.ExportPlan;

/** 导出配置 GUI。 */
public final class GuiExportConfig extends GuiScreen {

    private static final int PANEL_WIDTH = 380;
    private static final int PANEL_HEIGHT = 340;
    private static final int FIELD_WIDTH = 190;
    private static final int FIELD_HEIGHT = 18;
    private static final int FIELD_ROW_GAP = 28;
    private static final int OPTION_HEIGHT = 20;
    private static final int BUTTON_WIDTH = 78;
    private static final int BUTTON_HEIGHT = 18;
    private static final int HEADER_HEIGHT = 48;
    private static final int FOOTER_HEIGHT = 48;
    private static final int MIN_PANEL_HEIGHT = HEADER_HEIGHT + FOOTER_HEIGHT;

    private static final int HEADER_TITLE_TOP = 16;
    private static final int HEADER_SUBTITLE_TOP = 30;
    private static final int PLAN_SECTION_TOP = 4;
    private static final int PLAN_BUTTON_TOP = 24;
    private static final int PLAN_DESCRIPTION_TOP = 50;
    private static final int PARAMETER_SECTION_TOP = 62;
    private static final int FIELD_TOP = 80;
    private static final int FORMAT_SECTION_TOP = 160;
    private static final int FORMAT_BUTTON_TOP = 180;
    private static final int ASSET_SECTION_TOP = 206;
    private static final int ASSET_BUTTON_TOP = 224;
    private static final int CONTENT_BOTTOM_PADDING = 10;
    private static final int CONTENT_HEIGHT = ASSET_BUTTON_TOP + OPTION_HEIGHT + CONTENT_BOTTOM_PADDING;
    private static final int ACTION_BUTTON_BOTTOM_MARGIN = 12;
    private static final int SCROLL_STEP = 20;

    private static final int START_BUTTON_ID = 1;
    private static final int CANCEL_BUTTON_ID = 2;
    private static final int PLAN_ID_BASE = 100;
    private static final int FORMAT_ID_BASE = 200;
    private static final int ASSET_SKIP_IMAGES_ID = 300;
    private static final int ASSET_ANIMATION_SEQUENCE_ID = 301;
    private static final int ASSET_STATIC_FIRST_FRAME_ID = 302;

    private static final int COLOR_OVERLAY_TOP = 0xE0101420;
    private static final int COLOR_OVERLAY_BOTTOM = 0xF006080F;
    private static final int COLOR_PANEL = 0xEE151A25;
    private static final int COLOR_PANEL_EDGE = 0xFF2F7DD1;
    private static final int COLOR_TITLE = 0xFFFFFF;
    private static final int COLOR_SECTION = 0x7CCBFF;
    private static final int COLOR_TEXT = 0xD8E8FF;
    private static final int COLOR_SUBTEXT = 0x96A8BD;
    private static final int COLOR_ERROR = 0xFF7777;
    private static final int COLOR_BUTTON = 0xFF202838;
    private static final int COLOR_BUTTON_HOVER = 0xFF2C3A52;
    private static final int COLOR_BUTTON_SELECTED = 0xFF1F6FB2;
    private static final int COLOR_BUTTON_EDGE = 0xFF4AA3FF;
    private static final int COLOR_BUTTON_DISABLED = 0xFF303030;

    private ExportPlan selectedPlan = ExportPlan.ALL;
    private final EnumSet<BundleFormat> selectedFormats = EnumSet.of(BundleFormat.defaultFormat());
    private int selectedAssetModeId = ASSET_ANIMATION_SEQUENCE_ID;
    private int scrollY;
    private String errorMessage = "";

    private GuiTextField packSlugField;
    private GuiTextField packVersionField;
    private GuiTextField variantField;
    private GuiTextField[] fields = new GuiTextField[0];

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        buttonList.clear();

        addPlanButtons();
        initTextFields();
        addFormatButtons();
        addResourceButtons();
        buttonList.add(
            new StyledButton(
                START_BUTTON_ID,
                0,
                0,
                BUTTON_WIDTH,
                buttonHeight(),
                label("webnei.gui.export.config.start")));
        buttonList.add(
            new StyledButton(
                CANCEL_BUTTON_ID,
                0,
                0,
                BUTTON_WIDTH,
                buttonHeight(),
                label("webnei.gui.export.config.cancel")));
        clampScroll();
        layoutControls();
        refreshButtonStates();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == START_BUTTON_ID) {
            submit();
            return;
        }
        if (button.id == CANCEL_BUTTON_ID) {
            mc.displayGuiScreen(null);
            return;
        }
        if (button.id >= PLAN_ID_BASE && button.id < PLAN_ID_BASE + ExportPlan.values().length) {
            selectedPlan = ExportPlan.values()[button.id - PLAN_ID_BASE];
        } else if (button.id >= FORMAT_ID_BASE && button.id < FORMAT_ID_BASE + BundleFormat.values().length) {
            toggleFormat(BundleFormat.values()[button.id - FORMAT_ID_BASE]);
        } else if (button.id >= ASSET_SKIP_IMAGES_ID && button.id <= ASSET_STATIC_FIRST_FRAME_ID) {
            selectedAssetModeId = button.id;
        }
        errorMessage = "";
        refreshButtonStates();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            mc.displayGuiScreen(null);
            return;
        }
        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            submit();
            return;
        }
        for (GuiTextField field : fields) {
            field.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (GuiTextField field : fields) {
            if (field.getVisible()) {
                field.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel == 0 || maxScrollY() == 0) {
            return;
        }
        int mouseX = Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        if (!isInsideContentViewport(mouseX, mouseY)) {
            return;
        }
        scrollY += wheel > 0 ? -SCROLL_STEP : SCROLL_STEP;
        clampScroll();
        layoutControls();
    }

    @Override
    public void updateScreen() {
        for (GuiTextField field : fields) {
            field.updateCursorCounter();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        clampScroll();
        layoutControls();

        drawGradientRect(0, 0, width, height, COLOR_OVERLAY_TOP, COLOR_OVERLAY_BOTTOM);
        int left = panelLeft();
        int top = panelTop();
        drawPanel(left, top);

        int centerX = left + PANEL_WIDTH / 2;
        drawCenteredString(
            fontRendererObj,
            label("webnei.gui.export.config.title"),
            centerX,
            top + HEADER_TITLE_TOP,
            COLOR_TITLE);
        drawCenteredString(
            fontRendererObj,
            label("webnei.gui.export.config.subtitle"),
            centerX,
            top + HEADER_SUBTITLE_TOP,
            COLOR_SUBTEXT);

        int contentLeft = left + 24;
        drawContent(contentLeft);
        drawFooter(contentLeft, top + panelHeight() - FOOTER_HEIGHT);
        drawScrollBar(left);

        for (GuiTextField field : fields) {
            if (field.getVisible()) {
                field.drawTextBox();
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void addPlanButtons() {
        for (ExportPlan plan : ExportPlan.values()) {
            int id = PLAN_ID_BASE + plan.ordinal();
            buttonList.add(new StyledButton(id, 0, 0, 112, optionHeight(), planLabel(plan)));
        }
    }

    private void initTextFields() {
        packSlugField = textField(0);
        packVersionField = textField(1);
        variantField = textField(2);
        fields = new GuiTextField[] { packSlugField, packVersionField, variantField };
        packSlugField.setFocused(true);
    }

    private GuiTextField textField(int id) {
        GuiTextField field = new GuiTextField(fontRendererObj, 0, 0, FIELD_WIDTH, FIELD_HEIGHT);
        field.setMaxStringLength(96);
        return field;
    }

    private void addFormatButtons() {
        for (BundleFormat format : BundleFormat.values()) {
            int id = FORMAT_ID_BASE + format.ordinal();
            buttonList.add(new StyledButton(id, 0, 0, 84, optionHeight(), format.argumentName()));
        }
    }

    private void addResourceButtons() {
        int width = 104;
        buttonList.add(
            new StyledButton(
                ASSET_SKIP_IMAGES_ID,
                0,
                0,
                width,
                optionHeight(),
                label("webnei.gui.export.config.assets.skip")));
        buttonList.add(
            new StyledButton(
                ASSET_ANIMATION_SEQUENCE_ID,
                0,
                0,
                width,
                optionHeight(),
                label("webnei.gui.export.config.assets.animationSequence")));
        buttonList.add(
            new StyledButton(
                ASSET_STATIC_FIRST_FRAME_ID,
                0,
                0,
                width,
                optionHeight(),
                label("webnei.gui.export.config.assets.staticFirstFrame")));
    }

    private void refreshButtonStates() {
        for (Object entry : buttonList) {
            if (!(entry instanceof StyledButton)) {
                continue;
            }
            StyledButton button = (StyledButton) entry;
            button.setSelected(isSelected(button.id));
        }
        layoutControls();
    }

    private boolean isSelected(int id) {
        if (id >= PLAN_ID_BASE && id < PLAN_ID_BASE + ExportPlan.values().length) {
            return selectedPlan == ExportPlan.values()[id - PLAN_ID_BASE];
        }
        if (id >= FORMAT_ID_BASE && id < FORMAT_ID_BASE + BundleFormat.values().length) {
            return selectedFormats.contains(BundleFormat.values()[id - FORMAT_ID_BASE]);
        }
        if (id >= ASSET_SKIP_IMAGES_ID && id <= ASSET_STATIC_FIRST_FRAME_ID) {
            return selectedAssetModeId == id;
        }
        return false;
    }

    private void toggleFormat(BundleFormat format) {
        if (selectedFormats.contains(format)) {
            selectedFormats.remove(format);
        } else {
            selectedFormats.add(format);
        }
    }

    private void submit() {
        String packSlug = trim(packSlugField.getText());
        String packVersion = trim(packVersionField.getText());
        String variant = trim(variantField.getText());
        if (packSlug.isEmpty() || packVersion.isEmpty() || variant.isEmpty()) {
            errorMessage = label("webnei.gui.export.config.error.parameters");
            return;
        }
        if (selectedFormats.isEmpty()) {
            errorMessage = label("webnei.gui.export.config.error.formats");
            return;
        }
        Map<String, String> options = new LinkedHashMap<>();
        options.put(ExportRequestOptions.PACK_SLUG, packSlug);
        options.put(ExportRequestOptions.PACK_VERSION, packVersion);
        options.put(ExportRequestOptions.VARIANT, variant);
        if (selectedAssetModeId == ASSET_SKIP_IMAGES_ID) {
            options.put(ExportRequestOptions.SKIP_ASSET_RENDER, "true");
        }
        if (selectedAssetModeId == ASSET_STATIC_FIRST_FRAME_ID) {
            options.put(ExportRequestOptions.SKIP_ASSET_ANIMATIONS, "true");
        }
        ExportGuiLauncher.submitAndShowProgress(ExportRequest.bundle(selectedPlan.getId(), selectedFormats(), options));
    }

    private List<BundleFormat> selectedFormats() {
        List<BundleFormat> out = new ArrayList<>();
        for (BundleFormat format : BundleFormat.values()) {
            if (selectedFormats.contains(format)) {
                out.add(format);
            }
        }
        return out;
    }

    private void layoutControls() {
        int left = panelLeft();
        int top = panelTop();
        int contentLeft = left + 24;
        layoutTextFields(contentLeft);
        for (Object entry : buttonList) {
            if (!(entry instanceof StyledButton)) {
                continue;
            }
            layoutButton((StyledButton) entry, left, top, contentLeft);
        }
    }

    private void layoutTextFields(int contentLeft) {
        int fieldLeft = contentLeft + 118;
        int rowGap = fieldRowGap();
        layoutTextField(packSlugField, fieldLeft, contentY(FIELD_TOP));
        layoutTextField(packVersionField, fieldLeft, contentY(FIELD_TOP + rowGap));
        layoutTextField(variantField, fieldLeft, contentY(FIELD_TOP + rowGap * 2));
    }

    private void layoutTextField(GuiTextField field, int left, int top) {
        field.xPosition = left;
        field.yPosition = top;
        boolean visible = isContentControlVisible(top, FIELD_HEIGHT);
        field.setVisible(visible);
        if (!visible) {
            field.setFocused(false);
        }
    }

    private void layoutButton(StyledButton button, int left, int top, int contentLeft) {
        button.visible = true;
        if (button.id == START_BUTTON_ID) {
            button.xPosition = left + PANEL_WIDTH - 24 - BUTTON_WIDTH;
            button.yPosition = top + panelHeight() - ACTION_BUTTON_BOTTOM_MARGIN - buttonHeight();
            return;
        }
        if (button.id == CANCEL_BUTTON_ID) {
            button.xPosition = left + PANEL_WIDTH - 24 - BUTTON_WIDTH * 2 - 8;
            button.yPosition = top + panelHeight() - ACTION_BUTTON_BOTTOM_MARGIN - buttonHeight();
            return;
        }
        if (button.id >= PLAN_ID_BASE && button.id < PLAN_ID_BASE + ExportPlan.values().length) {
            int index = button.id - PLAN_ID_BASE;
            button.xPosition = contentLeft + index * 120;
            button.yPosition = contentY(PLAN_BUTTON_TOP);
        } else if (button.id >= FORMAT_ID_BASE && button.id < FORMAT_ID_BASE + BundleFormat.values().length) {
            int index = button.id - FORMAT_ID_BASE;
            button.xPosition = contentLeft + index * 92;
            button.yPosition = contentY(FORMAT_BUTTON_TOP);
        } else if (button.id >= ASSET_SKIP_IMAGES_ID && button.id <= ASSET_STATIC_FIRST_FRAME_ID) {
            int index = button.id - ASSET_SKIP_IMAGES_ID;
            int width = 104;
            int gap = 8;
            button.xPosition = contentLeft + index * (width + gap);
            button.yPosition = contentY(ASSET_BUTTON_TOP);
        }
        button.visible = isContentControlVisible(button.yPosition, button.height);
    }

    private void drawPanel(int left, int top) {
        int bottom = top + panelHeight();
        drawRect(left - 2, top - 2, left + PANEL_WIDTH + 2, bottom + 2, COLOR_PANEL_EDGE);
        drawRect(left, top, left + PANEL_WIDTH, bottom, COLOR_PANEL);
        drawRect(left, top, left + PANEL_WIDTH, top + HEADER_HEIGHT, 0x662F7DD1);
        drawRect(left, bottom - FOOTER_HEIGHT, left + PANEL_WIDTH, bottom - FOOTER_HEIGHT + 1, 0x553C6C99);
    }

    private void drawContent(int contentLeft) {
        drawContentSection(label("webnei.gui.export.config.plan"), contentLeft, PLAN_SECTION_TOP);
        drawContentPlanDescription(contentLeft, PLAN_DESCRIPTION_TOP);
        drawContentSection(label("webnei.gui.export.config.parameters"), contentLeft, PARAMETER_SECTION_TOP);
        drawFieldLabels(contentLeft, contentY(FIELD_TOP));
        drawContentSection(label("webnei.gui.export.config.formats"), contentLeft, FORMAT_SECTION_TOP);
        drawContentSection(label("webnei.gui.export.config.assets"), contentLeft, ASSET_SECTION_TOP);
    }

    private void drawFooter(int contentLeft, int footerTop) {
        if (!errorMessage.isEmpty()) {
            fontRendererObj.drawString(errorMessage, contentLeft, footerTop + 6, COLOR_ERROR);
        }
    }

    private void drawContentSection(String text, int left, int virtualTop) {
        int top = contentY(virtualTop);
        if (isContentControlVisible(top, 14)) {
            drawSection(text, left, top);
        }
    }

    private void drawSection(String text, int left, int top) {
        fontRendererObj.drawString(text, left, top, COLOR_SECTION);
        drawRect(left, top + 12, left + PANEL_WIDTH - 48, top + 13, 0x553C6C99);
    }

    private void drawContentPlanDescription(int left, int virtualTop) {
        int top = contentY(virtualTop);
        if (!isContentControlVisible(top, 8)) {
            return;
        }
        String description = StatCollector.translateToLocal(planDescriptionKey(selectedPlan));
        fontRendererObj.drawString(description, left, top, COLOR_SUBTEXT);
    }

    private void drawFieldLabels(int left, int top) {
        int rowGap = fieldRowGap();
        drawContentText(label("webnei.gui.export.config.packSlug"), left, top + 5, COLOR_TEXT);
        drawContentText(label("webnei.gui.export.config.packVersion"), left, top + rowGap + 5, COLOR_TEXT);
        drawContentText(label("webnei.gui.export.config.variant"), left, top + rowGap * 2 + 5, COLOR_TEXT);
    }

    private void drawContentText(String text, int left, int top, int color) {
        if (isContentControlVisible(top, 8)) {
            fontRendererObj.drawString(text, left, top, color);
        }
    }

    private void drawScrollBar(int panelLeft) {
        int maxScroll = maxScrollY();
        int viewportHeight = contentViewportHeight();
        if (maxScroll == 0 || viewportHeight <= 0) {
            return;
        }
        int trackLeft = panelLeft + PANEL_WIDTH - 10;
        int trackTop = contentViewportTop() + 2;
        int trackBottom = contentViewportBottom() - 2;
        int trackHeight = trackBottom - trackTop;
        if (trackHeight <= 0) {
            return;
        }
        int thumbHeight = Math.max(14, viewportHeight * viewportHeight / CONTENT_HEIGHT);
        thumbHeight = Math.min(thumbHeight, trackHeight);
        int thumbTravel = trackHeight - thumbHeight;
        int thumbTop = trackTop + Math.round(scrollY * thumbTravel / (float) maxScroll);
        drawRect(trackLeft, trackTop, trackLeft + 3, trackBottom, 0x553C6C99);
        drawRect(trackLeft, thumbTop, trackLeft + 3, thumbTop + thumbHeight, COLOR_BUTTON_EDGE);
    }

    private int panelLeft() {
        return (width - PANEL_WIDTH) / 2;
    }

    private int panelTop() {
        return Math.max(6, (height - panelHeight()) / 2);
    }

    private int panelHeight() {
        if (height <= 0) {
            return PANEL_HEIGHT;
        }
        int available = height - 12;
        if (available <= MIN_PANEL_HEIGHT) {
            return Math.max(HEADER_HEIGHT + FOOTER_HEIGHT, available);
        }
        return Math.min(PANEL_HEIGHT, available);
    }

    private int contentViewportTop() {
        return panelTop() + HEADER_HEIGHT;
    }

    private int contentViewportBottom() {
        return panelTop() + panelHeight() - FOOTER_HEIGHT;
    }

    private int contentViewportHeight() {
        return Math.max(0, contentViewportBottom() - contentViewportTop());
    }

    private int contentY(int value) {
        return contentViewportTop() + value - scrollY;
    }

    private int maxScrollY() {
        return Math.max(0, CONTENT_HEIGHT - contentViewportHeight());
    }

    private void clampScroll() {
        scrollY = Math.max(0, Math.min(scrollY, maxScrollY()));
    }

    private boolean isContentControlVisible(int top, int controlHeight) {
        return top >= contentViewportTop() && top + controlHeight <= contentViewportBottom();
    }

    private boolean isInsideContentViewport(int mouseX, int mouseY) {
        int left = panelLeft();
        return mouseX >= left && mouseX < left + PANEL_WIDTH
            && mouseY >= contentViewportTop()
            && mouseY < contentViewportBottom();
    }

    private int optionHeight() {
        return OPTION_HEIGHT;
    }

    private int buttonHeight() {
        return BUTTON_HEIGHT;
    }

    private int fieldRowGap() {
        return FIELD_ROW_GAP;
    }

    private static String planLabel(ExportPlan plan) {
        return StatCollector.translateToLocal(planLabelKey(plan));
    }

    private static String planLabelKey(ExportPlan plan) {
        return "webnei.task." + plan.getId();
    }

    private static String planDescriptionKey(ExportPlan plan) {
        return planLabelKey(plan) + ".description";
    }

    private static String label(String key) {
        return StatCollector.translateToLocal(key);
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private final class StyledButton extends GuiButton {

        private boolean selected;

        private StyledButton(int id, int x, int y, int width, int height, String label) {
            super(id, x, y, width, height, label);
        }

        private void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
            if (!visible) {
                return;
            }
            boolean hovered = mouseX >= xPosition && mouseY >= yPosition
                && mouseX < xPosition + width
                && mouseY < yPosition + height;
            int background = enabled ? selected ? COLOR_BUTTON_SELECTED : hovered ? COLOR_BUTTON_HOVER : COLOR_BUTTON
                : COLOR_BUTTON_DISABLED;
            drawRect(xPosition, yPosition, xPosition + width, yPosition + height, background);
            drawRect(xPosition, yPosition, xPosition + width, yPosition + 1, COLOR_BUTTON_EDGE);
            drawRect(xPosition, yPosition + height - 1, xPosition + width, yPosition + height, 0xFF0A0D14);
            int textColor = enabled ? COLOR_TITLE : COLOR_SUBTEXT;
            drawCenteredString(
                fontRendererObj,
                displayString,
                xPosition + width / 2,
                yPosition + (height - 8) / 2,
                textColor);
        }
    }
}
