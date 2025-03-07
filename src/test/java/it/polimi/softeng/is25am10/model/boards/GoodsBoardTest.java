package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.TilesType;
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
        board.setTile(2, 2, new Tile(TilesType.B_BOX_3, "uuuu"), 'n');
        board.setTile(1, 2, new Tile(TilesType.R_BOX_1, "uuuu"), 'n');

        ElementsPlaceholder boxes = new ElementsPlaceholder(board);
        boxes.set(2, 2, 2);

        other = new ArrayList<>();
        other.add(boxes);

        blockRed = new GoodsBoard(board, 'r');
        blockRed.setOthers(other);
    }

    @Test
    void testPut(){
        Result<Integer> res;

        res = blockRed.put(2, 2, 1);
        assertTrue(res.isErr());
        assertEquals("cant place here", res.getReason());
        assertEquals(0, blockRed.get(2, 2));

        res = blockRed.put(1, 2, 1);
        assertTrue(res.isOk());
        assertEquals(1, blockRed.get(1, 2));

        res = blockRed.put(1, 2, 1);
        assertTrue(res.isErr());
        assertEquals(1, blockRed.get(1, 2));
    }
}