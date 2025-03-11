package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BatteryBoardTest {
    TilesBoard board;
    BatteryBoard batteries;

    @BeforeEach
    void setUp() {
        board = new TilesBoard();
        board.setTile(new Coordinate(2, 2), new Tile(Tile.Type.BATTERY_3, "uuuu"), Tile.Rotation.NONE);
        board.setTile(new Coordinate(1, 2), new Tile(Tile.Type.HOUSE, "uuuu"), Tile.Rotation.NONE);
        board.setTile(new Coordinate(0, 2), new Tile(Tile.Type.BATTERY_2, "uuuu"), Tile.Rotation.NONE);
        batteries = new BatteryBoard(board);
    }

    @Test
    void testPut(){
        Result<Integer> res = batteries.put(new Coordinate(2, 2), 2);
        assertTrue(res.isOk());
        assertEquals(2, batteries.get(new Coordinate(2, 2)));

        res = batteries.put(new Coordinate(2, 2), 3);
        assertTrue(res.isErr());
        assertEquals("cant place here", res.getReason());

        res = batteries.put(new Coordinate(0, 2), 3);
        assertTrue(res.isErr());
        assertEquals("cant place here", res.getReason());

        res = batteries.put(new Coordinate(0, 2), 1);
        assertTrue(res.isOk());
        assertEquals(1, batteries.get(new Coordinate(0, 2)));

        res = batteries.put(new Coordinate(0, 2), 1);
        assertTrue(res.isOk());
        assertEquals(2, batteries.get(new Coordinate(0, 2)));

        res = batteries.put(new Coordinate(1, 2), 1);
        assertTrue(res.isErr());
        assertEquals("cant place here", res.getReason());
    }

}