package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShipContainerTest {
    @Test
    void testSHipContainer() {
        ShipContainer<Integer> ship = new ShipContainer<>(0);

        assertTrue(ship.set(0, 0, 10).isAccepted());
        assertFalse(ship.set(-1, -1, 10).isAccepted());
        assertEquals("out of bound", ship.set(-1, -1, 10).getReason());
        assertFalse(ship.set(7, 5, 10).isAccepted());

        assertEquals(10, (int) ship.get(0, 0).getData());
        assertFalse(ship.get(-1, -1).isAccepted());
        assertEquals("out of bound", ship.get(-1, -1).getReason());
        assertFalse(ship.get(7, 5).isAccepted());
    }
}