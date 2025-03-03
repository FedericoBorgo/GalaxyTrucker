package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BatteryBoardTest {
    ShipBoard shipBoard;

    @BeforeEach
    void configShipboard(){
        shipBoard = new ShipBoard();

        shipBoard.setTile(2, 2, new Tile(TilesType.BATTERY_2, "ssss"), 'n');
        shipBoard.setTile(1, 2, new Tile(TilesType.BATTERY_3, "ssss"), 'n');
        shipBoard.setTile(2, 2, new Tile(TilesType.ROCKET, "ssss"), 'n');
    }

    @Test
    void testBattery() {
        BatteryBoard batteryBoard = new BatteryBoard(shipBoard);
        Result<Integer> res;

        res = batteryBoard.put(2, 2, 2);
        assertTrue(res.isAccepted());
        assertEquals(2, res.getData());
        assertEquals(2, batteryBoard.get(2, 2).getData());
        assertEquals(2, batteryBoard.getTotal());

        res = batteryBoard.put(2, 2, 1);
        assertFalse(res.isAccepted());
        assertEquals("too many batteries", res.getReason());
        assertEquals(2, batteryBoard.getTotal());

        res = batteryBoard.move(2, 2, 1, 2, 2);
        assertTrue(res.isAccepted());
        assertEquals(2, res.getData());
        assertEquals(2, batteryBoard.get(1, 2).getData());
        assertEquals(2, batteryBoard.getTotal());

        res = batteryBoard.put(1, 2, 1);
        assertTrue(res.isAccepted());
        assertEquals(3, res.getData());
        assertEquals(3, batteryBoard.get(1, 2).getData());
        assertEquals(3, batteryBoard.getTotal());

        res = batteryBoard.remove(1, 2, 1);
        assertTrue(res.isAccepted());
        assertEquals(2, res.getData());
        assertEquals(2, batteryBoard.get(1, 2).getData());
        assertEquals(2, batteryBoard.getTotal());
    }
}