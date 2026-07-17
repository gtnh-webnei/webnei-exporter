package moe.takochan.webnei.exporter.domain.item.internal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.lwjgl.input.Keyboard;

class TooltipKeySimulatorTest {

    @Test
    void keyStatesHaveFixedOrderAndPersistenceValues() {
        assertArrayEquals(
            new TooltipKeyState[] { TooltipKeyState.NONE, TooltipKeyState.LSHIFT, TooltipKeyState.LCONTROL,
                TooltipKeyState.LSHIFT_LCONTROL },
            TooltipKeyState.values());
        assertEquals(
            Arrays.asList("none", "lshift", "lcontrol", "lshift_lcontrol"),
            Arrays.asList(
                TooltipKeyState.NONE.persistedKey(),
                TooltipKeyState.LSHIFT.persistedKey(),
                TooltipKeyState.LCONTROL.persistedKey(),
                TooltipKeyState.LSHIFT_LCONTROL.persistedKey()));
    }

    @Test
    void everyStateVerifiesReleasedThenExactTargetAndOnlyEndsOverride() {
        for (TooltipKeyState state : TooltipKeyState.values()) {
            FakeAccess access = new FakeAccess();
            TooltipKeySimulator simulator = new TooltipKeySimulator(access);

            String result = simulator.withState(state, () -> {
                assertEquals(state.leftShiftPressed(), access.shiftPressed);
                assertEquals(state.leftControlPressed(), access.controlPressed);
                return state.persistedKey();
            });

            assertEquals(state.persistedKey(), result);
            assertFalse(access.active);
            assertEquals(4, access.reads);
            assertEquals("begin", access.events.get(0));
            assertEquals("end", access.events.get(access.events.size() - 1));
        }
    }

    @Test
    void releasedSelfCheckMismatchSkipsActionAndClearsScope() {
        FakeAccess access = new FakeAccess();
        access.releasedMismatch = true;
        boolean[] actionCalled = { false };

        IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> new TooltipKeySimulator(access).withState(TooltipKeyState.NONE, () -> {
                actionCalled[0] = true;
                return null;
            }));

        assertTrue(
            thrown.getMessage()
                .contains("mismatch for released"));
        assertFalse(actionCalled[0]);
        assertFalse(access.active);
        assertEquals(Arrays.asList("begin", "end"), access.events);
    }

    @Test
    void targetSelfCheckMismatchSkipsActionAndClearsScope() {
        FakeAccess access = new FakeAccess();
        access.targetMismatch = true;
        boolean[] actionCalled = { false };

        IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> new TooltipKeySimulator(access).withState(TooltipKeyState.LSHIFT, () -> {
                actionCalled[0] = true;
                return null;
            }));

        assertTrue(
            thrown.getMessage()
                .contains("mismatch for lshift"));
        assertFalse(actionCalled[0]);
        assertFalse(access.active);
        assertEquals(Arrays.asList("begin", "lshift=true", "end"), access.events);
    }

    @Test
    void actionFailureRemainsPrimaryWhenEndAlsoFails() {
        FakeAccess access = new FakeAccess();
        access.failEnd = true;
        RuntimeException actionFailure = new RuntimeException("action");

        RuntimeException thrown = assertThrows(
            RuntimeException.class,
            () -> new TooltipKeySimulator(access).withState(TooltipKeyState.NONE, () -> { throw actionFailure; }));

        assertSame(actionFailure, thrown);
        assertEquals(1, thrown.getSuppressed().length);
        assertTrue(
            thrown.getSuppressed()[0].getMessage()
                .contains("Failed to end tooltip key override"));
    }

    @Test
    void endFailureIsPrimaryAfterSuccessfulAction() {
        FakeAccess access = new FakeAccess();
        access.failEnd = true;

        IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> new TooltipKeySimulator(access).withState(TooltipKeyState.NONE, () -> "done"));

        assertTrue(
            thrown.getMessage()
                .contains("Failed to end tooltip key override"));
    }

    @Test
    void beginFailureDoesNotClearAnotherScope() {
        FakeAccess access = new FakeAccess();
        access.failBegin = true;

        assertThrows(
            IllegalStateException.class,
            () -> new TooltipKeySimulator(access).withState(TooltipKeyState.NONE, () -> "unused"));

        assertEquals(Arrays.asList("begin"), access.events);
        assertEquals(0, access.endCalls);
    }

    private static final class FakeAccess implements TooltipKeySimulator.Access {

        private boolean shiftPressed;
        private boolean controlPressed;
        private boolean active;
        private boolean failBegin;
        private boolean failEnd;
        private boolean releasedMismatch;
        private boolean targetMismatch;
        private int reads;
        private int endCalls;
        private final List<String> events = new ArrayList<>();

        @Override
        public boolean isPressed(int lwjglKey) {
            reads++;
            if (releasedMismatch && reads == 1) {
                return true;
            }
            if (targetMismatch && reads == 3) {
                return false;
            }
            if (lwjglKey == Keyboard.KEY_LSHIFT) {
                return shiftPressed;
            }
            if (lwjglKey == Keyboard.KEY_LCONTROL) {
                return controlPressed;
            }
            throw new AssertionError("Unexpected key " + lwjglKey);
        }

        @Override
        public void beginReleased() {
            events.add("begin");
            if (failBegin) {
                throw new RuntimeException("begin failure");
            }
            active = true;
            shiftPressed = false;
            controlPressed = false;
        }

        @Override
        public void setPressed(int lwjglKey, boolean pressed) {
            if (lwjglKey == Keyboard.KEY_LSHIFT) {
                shiftPressed = pressed;
                events.add("lshift=" + pressed);
            } else if (lwjglKey == Keyboard.KEY_LCONTROL) {
                controlPressed = pressed;
                events.add("lcontrol=" + pressed);
            } else {
                throw new AssertionError("Unexpected key " + lwjglKey);
            }
        }

        @Override
        public void endOverride() {
            endCalls++;
            events.add("end");
            active = false;
            if (failEnd) {
                throw new RuntimeException("end failure");
            }
        }
    }
}
