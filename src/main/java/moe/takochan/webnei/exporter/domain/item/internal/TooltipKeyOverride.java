package moe.takochan.webnei.exporter.domain.item.internal;

import org.lwjgl.input.Keyboard;

/** Scoped query override for the managed tooltip modifier keys. */
public final class TooltipKeyOverride {

    private static boolean active;
    private static boolean leftShiftPressed;
    private static boolean leftControlPressed;

    private TooltipKeyOverride() {}

    public static synchronized Boolean overrideFor(int lwjglKey) {
        if (!active) {
            return null;
        }
        if (lwjglKey == Keyboard.KEY_LSHIFT) {
            return Boolean.valueOf(leftShiftPressed);
        }
        if (lwjglKey == Keyboard.KEY_LCONTROL) {
            return Boolean.valueOf(leftControlPressed);
        }
        return null;
    }

    public static synchronized void activateReleased() {
        if (active) {
            throw new IllegalStateException("Tooltip key override is already active");
        }
        leftShiftPressed = false;
        leftControlPressed = false;
        active = true;
    }

    public static synchronized void setPressed(int lwjglKey, boolean pressed) {
        if (lwjglKey != Keyboard.KEY_LSHIFT && lwjglKey != Keyboard.KEY_LCONTROL) {
            throw new IllegalArgumentException("Unsupported tooltip key " + lwjglKey);
        }
        if (!active) {
            throw new IllegalStateException("Tooltip key override is not active");
        }
        if (lwjglKey == Keyboard.KEY_LSHIFT) {
            leftShiftPressed = pressed;
        } else {
            leftControlPressed = pressed;
        }
    }

    public static synchronized void clear() {
        active = false;
        leftShiftPressed = false;
        leftControlPressed = false;
    }
}
