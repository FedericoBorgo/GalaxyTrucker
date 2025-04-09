package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BatteryBoardTest {
    TilesBoard tiles;
    BatteryBoard batteries;

    @BeforeEach
    void setUp() {
        tiles = new TilesBoard();
        //yx0  1     2  3
        //2 B2 HOUSE B3 C_HOUSe
        tiles.setTile(new Coordinate(2, 2), new Tile(Tile.Type.BATTERY_3, "uuuu"), Tile.Rotation.NONE);
        tiles.setTile(new Coordinate(1, 2), new Tile(Tile.Type.HOUSE, "uuuu"), Tile.Rotation.NONE);
        tiles.setTile(new Coordinate(0, 2), new Tile(Tile.Type.BATTERY_2, "uuuu"), Tile.Rotation.NONE);
        batteries = new BatteryBoard(tiles);
    }

    @Test
    void testPut(){
        // 3 batteries can be placed here
        Result<Integer> res = batteries.put(new Coordinate(2, 2), 2);
        assertTrue(res.isOk());
        assertEquals(2, batteries.get(new Coordinate(2, 2)));

        // too many batteries here
        res = batteries.put(new Coordinate(2, 2), 3);
        assertTrue(res.isErr());
        assertEquals("cant place here", res.getReason());

        // too many batteries
        res = batteries.put(new Coordinate(0, 2), 3);
        assertTrue(res.isErr());
        assertEquals("cant place here", res.getReason());

        // ok
        res = batteries.put(new Coordinate(0, 2), 1);
        assertTrue(res.isOk());
        assertEquals(1, batteries.get(new Coordinate(0, 2)));

        // ok
        res = batteries.put(new Coordinate(0, 2), 1);
        assertTrue(res.isOk());
        assertEquals(2, batteries.get(new Coordinate(0, 2)));

        // too many batteries
        res = batteries.put(new Coordinate(1, 2), 1);
        assertTrue(res.isErr());
        assertEquals("cant place here", res.getReason());
    }

}