package moe.takochan.webnei.exporter.domain.item.internal;

import org.lwjgl.input.Keyboard;

/** 在单次 tooltip 采集期间同步模拟左 Shift 与左 Control 键态。 */
final class TooltipKeySimulator {

    private static final int LEFT_SHIFT = Keyboard.KEY_LSHIFT;
    private static final int LEFT_CONTROL = Keyboard.KEY_LCONTROL;

    interface Access {

        boolean isPressed(int lwjglKey) throws Throwable;

        void beginReleased() throws Throwable;

        void setPressed(int lwjglKey, boolean pressed) throws Throwable;

        void endOverride() throws Throwable;
    }

    interface Action<T> {

        T run() throws Throwable;
    }

    private final Access access;

    TooltipKeySimulator() {
        this(new KeyboardAccess());
    }

    TooltipKeySimulator(Access access) {
        this.access = access;
    }

    synchronized <T> T withState(TooltipKeyState state, Action<T> action) {
        boolean overrideActive = false;
        T result = null;
        Throwable primaryFailure = null;
        try {
            try {
                access.beginReleased();
            } catch (Throwable failure) {
                throw new IllegalStateException(
                    "Failed to begin tooltip key override " + state.persistedKey(),
                    failure);
            }
            overrideActive = true;
            verifyState(false, false, "released");
            setTargetState(state);
            verifyState(state.leftShiftPressed(), state.leftControlPressed(), state.persistedKey());
            result = action.run();
        } catch (Throwable failure) {
            primaryFailure = failure;
        }

        if (overrideActive) {
            IllegalStateException endFailure = endOverride();
            if (endFailure != null) {
                if (primaryFailure != null) {
                    primaryFailure.addSuppressed(endFailure);
                } else {
                    throw endFailure;
                }
            }
        }
        if (primaryFailure != null) {
            throw propagate(primaryFailure);
        }
        return result;
    }

    private void setTargetState(TooltipKeyState state) {
        try {
            if (state.leftShiftPressed()) {
                access.setPressed(LEFT_SHIFT, true);
            }
            if (state.leftControlPressed()) {
                access.setPressed(LEFT_CONTROL, true);
            }
        } catch (Throwable failure) {
            throw new IllegalStateException("Failed to set tooltip key state " + state.persistedKey(), failure);
        }
    }

    private void verifyState(boolean expectedShift, boolean expectedControl, String phase) {
        final boolean actualShift;
        final boolean actualControl;
        try {
            actualShift = access.isPressed(LEFT_SHIFT);
            actualControl = access.isPressed(LEFT_CONTROL);
        } catch (Throwable failure) {
            throw new IllegalStateException("Failed to verify tooltip key state " + phase, failure);
        }
        if (actualShift != expectedShift || actualControl != expectedControl) {
            throw new IllegalStateException(
                "Tooltip key state mismatch for " + phase
                    + ": expected shift="
                    + expectedShift
                    + ", control="
                    + expectedControl
                    + ", actual shift="
                    + actualShift
                    + ", control="
                    + actualControl);
        }
    }

    private IllegalStateException endOverride() {
        try {
            access.endOverride();
            return null;
        } catch (Throwable failure) {
            return new IllegalStateException("Failed to end tooltip key override", failure);
        }
    }

    private static RuntimeException propagate(Throwable failure) {
        TooltipKeySimulator.<RuntimeException>throwUnchecked(failure);
        throw new AssertionError("unreachable");
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throwUnchecked(Throwable failure) throws T {
        throw (T) failure;
    }

    private static final class KeyboardAccess implements Access {

        @Override
        public boolean isPressed(int lwjglKey) {
            return Keyboard.isKeyDown(lwjglKey);
        }

        @Override
        public void beginReleased() {
            TooltipKeyOverride.activateReleased();
        }

        @Override
        public void setPressed(int lwjglKey, boolean pressed) {
            TooltipKeyOverride.setPressed(lwjglKey, pressed);
        }

        @Override
        public void endOverride() {
            TooltipKeyOverride.clear();
        }
    }
}
