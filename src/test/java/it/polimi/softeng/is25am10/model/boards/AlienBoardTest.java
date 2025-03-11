package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
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
        board.setTile(new Coordinate(2, 2), new Tile(Tile.Type.HOUSE, "uuuu"), Tile.Rotation.NONE);
        board.setTile(new Coordinate(2, 1), new Tile(Tile.Type.HOUSE, "uuuu"), Tile.Rotation.NONE);
        board.setTile(new Coordinate(1, 2), new Tile(Tile.Type.P_ADDON, "uuuu"), Tile.Rotation.NONE);
        board.setTile(new Coordinate(0, 2), new Tile(Tile.Type.ROCKET, "uuuu"), Tile.Rotation.NONE);
        board.setTile(new Coordinate(0, 3), new Tile(Tile.Type.HOUSE, "uuuu"), Tile.Rotation.NONE);

        ElementsPlaceholder astronaut = new ElementsPlaceholder(board);
        astronaut.set(new Coordinate(2, 1), 1);

        other = new ArrayList<>();
        other.add(astronaut);


        alien = new AlienBoard(board, AlienBoard.Type.PURPLE);
        alien.setOthers(other);
    }

    @Test
    void testPut(){
        Result<Integer> res = alien.put(new Coordinate(2, 1), 1);
        assertTrue(res.isErr());

        res = alien.put(new Coordinate(2, 2), 1);
        assertEquals(1, alien.get(new Coordinate(2, 2)));
        assertTrue(res.isOk());

        res = alien.put(new Coordinate(2, 2), 1);
        assertEquals(1, alien.get(new Coordinate(2, 2)));
        assertTrue(res.isErr());

        res = alien.put(new Coordinate(0, 2), 1);
        assertEquals(0, alien.get(new Coordinate(0, 2)));
        assertTrue(res.isErr());

        res = alien.put(new Coordinate(0, 3), 1);
        assertEquals(0, alien.get(new Coordinate(0, 3)));
        assertTrue(res.isErr());
    }
}