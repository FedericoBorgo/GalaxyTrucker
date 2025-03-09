package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.TilesType;
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
        board.setTile(2, 2, new Tile(TilesType.HOUSE, "uuuu"), 'n');
        board.setTile(1, 2, new Tile(TilesType.HOUSE, "uuuu"), 'n');
        board.setTile(0, 2, new Tile(TilesType.ROCKET, "uuuu"), 'n');

        ElementsPlaceholder alien = new ElementsPlaceholder(board);
        alien.set(1, 2, 1);

        other = new ArrayList<>();
        other.add(alien);


        astronaut = new AstronautBoard(board);
        astronaut.setOthers(other);
    }

    @Test
    void testPutGet(){
        Result<Integer> result = astronaut.put(2, 2, 2);
        assertTrue(result.isOk());
        assertEquals(2, astronaut.get(2, 2));

        result = astronaut.put(3, 2, 2);
        assertTrue(result.isOk());
        assertEquals(2, astronaut.get(2, 2));

        assertEquals(4, astronaut.getTotal());

        result = astronaut.put(2, 2, 1);
        assertTrue(result.isErr());
        assertEquals("cant place here", result.getReason());

        result = astronaut.put(1, 2, 1);
        assertTrue(result.isErr());
        assertEquals("occupied by others", result.getReason());

        result = astronaut.put(0, 2, 1);
        assertTrue(result.isErr());
        assertEquals("cant place here", result.getReason());
    }

}