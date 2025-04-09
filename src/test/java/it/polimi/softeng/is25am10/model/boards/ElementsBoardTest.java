package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class ElementsBoardTest {
    ElementsPlaceholder elements;
    TilesBoard tiles;

    @BeforeEach
    void setUp() {
        tiles = new TilesBoard();
        elements = new ElementsPlaceholder(tiles);
    }

    @Test
    void testSetGet(){
        elements.put(new Coordinate(3, 2), 10);
        assertEquals(10, elements.get(new Coordinate(3, 2)));
        assertEquals(10, elements.getTotal());
    }

    @Test
    void testRemove(){
        elements.put(new Coordinate(3, 2), 10);
        elements.remove(new Coordinate(3, 2), 5);
        assertEquals(5, elements.get(new Coordinate(3, 2)));
        assertEquals(5, elements.getTotal());
    }

    @Test
    void testRemoveIllegals(){
        tiles.setTile(new Coordinate(2, 2), new Tile(Tile.Type.HOUSE, "uuuu"), Tile.Rotation.NONE);
        elements.put(new Coordinate(2, 2), 10);
        tiles.remove(new Coordinate(2, 2));
        List<Coordinate> res = elements.removeIllegals();
        assertTrue(res.contains(new Coordinate(2, 2)));
    }
}