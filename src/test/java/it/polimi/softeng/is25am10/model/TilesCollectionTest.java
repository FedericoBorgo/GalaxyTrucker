package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TilesCollectionTest {
    @Test
    void testTilesCollection() {
        TilesCollection tilesCollection = new TilesCollection();

        Tile tile1 = tilesCollection.getFromSeen(0);
        assertSame(Tile.EMPY_TILE, tile1);

        tile1 = tilesCollection.getNew();
        assertNotSame(Tile.EMPY_TILE, tile1);

        tilesCollection.give(tile1);
        assertSame(tile1, tilesCollection.getFromSeen(0));

        assertTrue(tilesCollection.getSeen().isEmpty());
    }

}