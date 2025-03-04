package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BatteryBoardTest {
    ShipBoard board;
    BatteryBoard batteries;

    @BeforeEach
    void setUp() {
        board = new ShipBoard();
        board.setTile(2, 2, new Tile(TilesType.BATTERY_3, "uuuu"), 'n');
        board.setTile(1, 2, new Tile(TilesType.HOUSE, "uuuu"), 'n');
        board.setTile(0, 2, new Tile(TilesType.BATTERY_2, "uuuu"), 'n');
        batteries = new BatteryBoard(board);
    }

    @Test
    void testPut(){
        Result<Integer> res = batteries.put(2, 2, 2);
        assertTrue(res.isOk());
        assertEquals(2, batteries.get(2, 2));

        res = batteries.put(2, 2, 3);
        assertTrue(res.isErr());
        assertEquals("too many batteries", res.getReason());

        res = batteries.put(0, 2, 3);
        assertTrue(res.isErr());
        assertEquals("too many batteries", res.getReason());

        res = batteries.put(0, 2, 1);
        assertTrue(res.isOk());
        assertEquals(1, batteries.get(0, 2));

        res = batteries.put(0, 2, 1);
        assertTrue(res.isOk());
        assertEquals(2, batteries.get(0, 2));

        res = batteries.put(1, 2, 1);
        assertTrue(res.isErr());
        assertEquals("tile is not a Battery Tile", res.getReason());
    }

}