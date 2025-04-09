package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AstronautBoardTest {
    TilesBoard tiles;
    AstronautBoard astronaut;
    List<ElementsBoard> other;

    @BeforeEach
    void setUp() {
        tiles = new TilesBoard();

        //yx0      1        2     3
        //2 ENGINE HOUSE(a) HOUSE C_HOUSE
        tiles.setTile(new Coordinate(2, 2), new Tile(Tile.Type.HOUSE, "uuuu"), Tile.Rotation.NONE);
        tiles.setTile(new Coordinate(1, 2), new Tile(Tile.Type.HOUSE, "uuuu"), Tile.Rotation.NONE);
        tiles.setTile(new Coordinate(0, 2), new Tile(Tile.Type.ENGINE, "uuuu"), Tile.Rotation.NONE);

        // place an alien 1t x1y2
        ElementsPlaceholder alien = new ElementsPlaceholder(tiles);
        alien.set(new Coordinate(1, 2), 1);

        other = new ArrayList<>();
        other.add(alien);

        // create the astronaut layer
        astronaut = new AstronautBoard(tiles);
        astronaut.setOthers(other);
    }

    @Test
    void testPutGet(){
        // there is no alien, it is a house and there are enough spaces.
        Result<Integer> result = astronaut.put(new Coordinate(2, 2), 2);
        assertTrue(result.isOk());
        assertEquals(2, astronaut.get(new Coordinate(2, 2)));

        // this is a central house
        result = astronaut.put(new Coordinate(3, 2), 2);
        assertTrue(result.isOk());
        assertEquals(2, astronaut.get(new Coordinate(2, 2)));

        assertEquals(4, astronaut.getTotal());

        // there are to many astronaut here
        result = astronaut.put(new Coordinate(2, 2), 1);
        assertTrue(result.isErr());
        assertEquals("cant place here", result.getReason());

        // this house is occupied by an alien
        result = astronaut.put(new Coordinate(1, 2), 1);
        assertTrue(result.isErr());
        assertEquals("occupied by others", result.getReason());

        // this is not an house
        result = astronaut.put(new Coordinate(0, 2), 1);
        assertTrue(result.isErr());
        assertEquals("cant place here", result.getReason());
    }

}