package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.TilesType;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class ElementsBoardTest {
    ElementsPlaceholder placeholder;
    TilesBoard board;

    @BeforeEach
    void setUp() {
        board = new TilesBoard();

        placeholder = new ElementsPlaceholder(board);
    }

    @Test
    void testSetGet(){
        placeholder.put(new Coordinate(3, 2), 10);
        assertEquals(10, placeholder.get(new Coordinate(3, 2)));
        assertEquals(10, placeholder.getTotal());
    }

    @Test
    void testRemove(){
        placeholder.put(new Coordinate(3, 2), 10);
        placeholder.remove(new Coordinate(3, 2), 5);
        assertEquals(5, placeholder.get(new Coordinate(3, 2)));
        assertEquals(5, placeholder.getTotal());
    }

    @Test
    void testMove(){
        board.setTile(new Coordinate(2, 2), new Tile(TilesType.HOUSE, "uuuu"), Tile.Rotation.NONE);
        placeholder.put(new Coordinate(3, 2), 10);
        placeholder.move(new Coordinate(3, 2), new Coordinate(2, 2), 5);

        assertEquals(5, placeholder.get(new Coordinate(3, 2)));
        assertEquals(5, placeholder.get(new Coordinate(2, 2)));
        assertEquals(10, placeholder.getTotal());
        assertEquals(10, placeholder.getTotal());
    }

    @Test
    void testRemoveIllegals(){
        board.setTile(new Coordinate(2, 2), new Tile(TilesType.HOUSE, "uuuu"), Tile.Rotation.NONE);
        placeholder.put(new Coordinate(2, 2), 10);
        board.remove(new Coordinate(2, 2));
        List<Coordinate> res = placeholder.removeIllegals();
        assertTrue(res.contains(new Coordinate(2, 2)));
    }
}