package moe.takochan.webnei.exporter.domain.item.internal;

/** Tooltip 采集时模拟的受管理键态，声明顺序即持久化采集顺序。 */
enum TooltipKeyState {

    NONE("none", "standard", false, false),
    LSHIFT("lshift", "key", true, false),
    LCONTROL("lcontrol", "key", false, true),
    LSHIFT_LCONTROL("lshift_lcontrol", "key", true, true);

    private final String persistedKey;
    private final String tooltipType;
    private final boolean leftShiftPressed;
    private final boolean leftControlPressed;

    TooltipKeyState(String persistedKey, String tooltipType, boolean leftShiftPressed, boolean leftControlPressed) {
        this.persistedKey = persistedKey;
        this.tooltipType = tooltipType;
        this.leftShiftPressed = leftShiftPressed;
        this.leftControlPressed = leftControlPressed;
    }

    String persistedKey() {
        return persistedKey;
    }

    String tooltipType() {
        return tooltipType;
    }

    boolean leftShiftPressed() {
        return leftShiftPressed;
    }

    boolean leftControlPressed() {
        return leftControlPressed;
    }
}
