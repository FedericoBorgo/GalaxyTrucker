package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.TilesType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlienBoardTest {
    TilesBoard board;
    AlienBoard alien;
    List<ElementsBoard> other;

    @BeforeEach
    void setUp() {
        board = new TilesBoard();
        board.setTile(2, 2, new Tile(TilesType.HOUSE, "uuuu"), 'n');
        board.setTile(2, 1, new Tile(TilesType.HOUSE, "uuuu"), 'n');
        board.setTile(1, 2, new Tile(TilesType.P_ADDON, "uuuu"), 'n');
        board.setTile(0, 2, new Tile(TilesType.ROCKET, "uuuu"), 'n');
        board.setTile(0, 3, new Tile(TilesType.HOUSE, "uuuu"), 'n');

        ElementsPlaceholder astronaut = new ElementsPlaceholder(board);
        astronaut.set(2, 1, 1);

        other = new ArrayList<>();
        other.add(astronaut);


        alien = new AlienBoard(board, 'p');
        alien.setOthers(other);
    }

    @Test
    void testPut(){
        Result<Integer> res = alien.put(2, 1, 1);
        assertTrue(res.isErr());

        res = alien.put(2, 2, 1);
        assertEquals(1, alien.get(2, 2));
        assertTrue(res.isOk());

        res = alien.put(2, 2, 1);
        assertEquals(1, alien.get(2, 2));
        assertTrue(res.isErr());

        res = alien.put(0, 2, 1);
        assertEquals(0, alien.get(0, 2));
        assertTrue(res.isErr());

        res = alien.put(0, 3, 1);
        assertEquals(0, alien.get(0, 3));
        assertTrue(res.isErr());
    }
}