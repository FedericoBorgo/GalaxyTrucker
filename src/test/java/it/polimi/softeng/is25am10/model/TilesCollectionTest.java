package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TilesCollectionTest {
    @Test
    void testDraw() {
        TilesCollection tilesCollection = new TilesCollection();
        Tile tile1 = tilesCollection.getNew();
        assertNotNull(tile1);
        assertNotSame(Tile.EMPY_TILE, tile1);
    }

    @Test
    void testSeen(){
        TilesCollection tilesCollection = new TilesCollection();

        Tile tile1 = tilesCollection.getFromSeen(new Tile(TilesType.HOUSE, "uuuu"));
        assertEquals(Tile.EMPY_TILE, tile1);
        tilesCollection.give(tile1);
        assertEquals(tile1, tilesCollection.getSeen().getFirst());
        assertEquals(tile1, tilesCollection.getFromSeen(tile1));
        assertTrue(tilesCollection.getSeen().isEmpty());
    }

}