package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AstronautBoardTest {
    TilesBoard board;
    AstronautBoard astronaut;
    List<ElementsBoard> other;

    @BeforeEach
    void setUp() {
        board = new TilesBoard();
        board.setTile(new Coordinate(2, 2), new Tile(Tile.Type.HOUSE, "uuuu"), Tile.Rotation.NONE);
        board.setTile(new Coordinate(1, 2), new Tile(Tile.Type.HOUSE, "uuuu"), Tile.Rotation.NONE);
        board.setTile(new Coordinate(0, 2), new Tile(Tile.Type.ENGINE, "uuuu"), Tile.Rotation.NONE);

        ElementsPlaceholder alien = new ElementsPlaceholder(board);
        alien.set(new Coordinate(1, 2), 1);

        other = new ArrayList<>();
        other.add(alien);


        astronaut = new AstronautBoard(board);
        astronaut.setOthers(other);
    }

    @Test
    void testPutGet(){
        Result<Integer> result = astronaut.put(new Coordinate(2, 2), 2);
        assertTrue(result.isOk());
        assertEquals(2, astronaut.get(new Coordinate(2, 2)));

        result = astronaut.put(new Coordinate(3, 2), 2);
        assertTrue(result.isOk());
        assertEquals(2, astronaut.get(new Coordinate(2, 2)));

        assertEquals(4, astronaut.getTotal());

        result = astronaut.put(new Coordinate(2, 2), 1);
        assertTrue(result.isErr());
        assertEquals("cant place here", result.getReason());

        result = astronaut.put(new Coordinate(1, 2), 1);
        assertTrue(result.isErr());
        assertEquals("occupied by others", result.getReason());

        result = astronaut.put(new Coordinate(0, 2), 1);
        assertTrue(result.isErr());
        assertEquals("cant place here", result.getReason());
    }

}