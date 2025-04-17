package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TilesCollectionTest {
    TilesCollection tilesCollection;

    @BeforeEach
    void setUp() {
        tilesCollection = new TilesCollection();
    }


    @Test
    void testDraw() {
        Tile tile1 = tilesCollection.getNew();
        assertNotNull(tile1);
        assertNotSame(Tile.Type.EMPTY, tile1.getType());
    }

    @Test
    void testSeen(){
        Tile tile1 = tilesCollection.getFromSeen(new Tile(Tile.Type.HOUSE, "uuuu"));
        assertEquals(Tile.Type.EMPTY, tile1.getType());
        tilesCollection.give(tile1);
        assertEquals(tile1, tilesCollection.getSeen().getFirst());
        assertEquals(tile1, tilesCollection.getFromSeen(tile1));
        assertTrue(tilesCollection.getSeen().isEmpty());
    }

}