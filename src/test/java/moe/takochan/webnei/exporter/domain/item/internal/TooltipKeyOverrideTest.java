package moe.takochan.webnei.exporter.domain.item.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.input.Keyboard;

class TooltipKeyOverrideTest {

    @BeforeEach
    @AfterEach
    void clearOverride() {
        TooltipKeyOverride.clear();
    }

    @Test
    void inactiveKeysHaveNoOverride() {
        assertNull(TooltipKeyOverride.overrideFor(Keyboard.KEY_LSHIFT));
        assertNull(TooltipKeyOverride.overrideFor(Keyboard.KEY_LCONTROL));
        assertNull(TooltipKeyOverride.overrideFor(Keyboard.KEY_A));
    }

    @Test
    void activationReleasesBothManagedKeysAndAllowsUpdates() {
        TooltipKeyOverride.activateReleased();

        assertFalse(TooltipKeyOverride.overrideFor(Keyboard.KEY_LSHIFT));
        assertFalse(TooltipKeyOverride.overrideFor(Keyboard.KEY_LCONTROL));

        TooltipKeyOverride.setPressed(Keyboard.KEY_LSHIFT, true);
        TooltipKeyOverride.setPressed(Keyboard.KEY_LCONTROL, true);

        assertTrue(TooltipKeyOverride.overrideFor(Keyboard.KEY_LSHIFT));
        assertTrue(TooltipKeyOverride.overrideFor(Keyboard.KEY_LCONTROL));
    }

    @Test
    void unmanagedKeysAreNeverOverriddenAndCannotBeSet() {
        TooltipKeyOverride.activateReleased();

        assertNull(TooltipKeyOverride.overrideFor(Keyboard.KEY_A));
        assertThrows(IllegalArgumentException.class, () -> TooltipKeyOverride.setPressed(Keyboard.KEY_A, true));
    }

    @Test
    void managedKeysCannotBeSetWhileInactive() {
        assertThrows(IllegalStateException.class, () -> TooltipKeyOverride.setPressed(Keyboard.KEY_LSHIFT, true));
    }

    @Test
    void nestedActivationDoesNotReplaceActiveScope() {
        TooltipKeyOverride.activateReleased();
        TooltipKeyOverride.setPressed(Keyboard.KEY_LSHIFT, true);

        assertThrows(IllegalStateException.class, TooltipKeyOverride::activateReleased);
        assertEquals(Boolean.TRUE, TooltipKeyOverride.overrideFor(Keyboard.KEY_LSHIFT));
        assertEquals(Boolean.FALSE, TooltipKeyOverride.overrideFor(Keyboard.KEY_LCONTROL));
    }

    @Test
    void clearReturnsToInactiveState() {
        TooltipKeyOverride.activateReleased();
        TooltipKeyOverride.setPressed(Keyboard.KEY_LSHIFT, true);

        TooltipKeyOverride.clear();

        assertNull(TooltipKeyOverride.overrideFor(Keyboard.KEY_LSHIFT));
        assertNull(TooltipKeyOverride.overrideFor(Keyboard.KEY_LCONTROL));
    }
}
