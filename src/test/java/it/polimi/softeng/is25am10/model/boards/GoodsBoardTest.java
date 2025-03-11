package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GoodsBoardTest {
    TilesBoard board;
    GoodsBoard blockRed;
    List<ElementsBoard> other;

    @BeforeEach
    void setUp() {
        board = new TilesBoard();
        board.setTile(new Coordinate(2, 2), new Tile(Tile.Type.B_BOX_3, "uuuu"), Tile.Rotation.NONE);
        board.setTile(new Coordinate(1, 2), new Tile(Tile.Type.R_BOX_1, "uuuu"), Tile.Rotation.NONE);

        ElementsPlaceholder boxes = new ElementsPlaceholder(board);
        boxes.set(new Coordinate(2, 2), 2);

        other = new ArrayList<>();
        other.add(boxes);

        blockRed = new GoodsBoard(board, GoodsBoard.Type.RED);
        blockRed.setOthers(other);
    }

    @Test
    void testPut(){
        Result<Integer> res;

        res = blockRed.put(new Coordinate(2, 2), 1);
        assertTrue(res.isErr());
        assertEquals("occupied by others", res.getReason());
        assertEquals(0, blockRed.get(new Coordinate(2, 2)));

        res = blockRed.put(new Coordinate(1, 2), 1);
        assertTrue(res.isOk());
        assertEquals(1, blockRed.get(new Coordinate(1, 2)));

        res = blockRed.put(new Coordinate(1, 2), 1);
        assertTrue(res.isErr());
        assertEquals(1, blockRed.get(new Coordinate(1, 2)));
    }
}