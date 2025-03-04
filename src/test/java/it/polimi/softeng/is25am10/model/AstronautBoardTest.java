package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AstronautBoardTest {
    ShipBoard board;
    AstronautBoard astronaut;
    List<ElementsBoard> other;

    @BeforeEach
    void setUp() {
        board = new ShipBoard();
        board.setTile(2, 2, new Tile(TilesType.HOUSE, "uuuu"), 'n');
        board.setTile(1, 2, new Tile(TilesType.HOUSE, "uuuu"), 'n');
        board.setTile(0, 2, new Tile(TilesType.ROCKET, "uuuu"), 'n');

        ElementsPlaceholder alien = new ElementsPlaceholder(board);
        alien.set(1, 2, 1);

        other = new ArrayList<>();
        other.add(alien);


        astronaut = new AstronautBoard(board);
        astronaut.setBoards(other);
    }

    @Test
    void testPut(){
        Result<Integer> result = astronaut.put(2, 2, 2);

        assertTrue(result.isOk());
        assertEquals(2, astronaut.get(2, 2));

        result = astronaut.put(2, 2, 1);
        assertFalse(result.isOk());
        assertEquals("too many astronauts", result.getReason());

        result = astronaut.put(1, 2, 1);
        assertFalse(result.isOk());
        assertEquals("occupied by alien", result.getReason());

        result = astronaut.put(0, 2, 1);
        assertFalse(result.isOk());
        assertEquals("cant place here", result.getReason());
    }

}