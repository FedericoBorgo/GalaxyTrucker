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

    @Test
    void testGetEmpty() {
        // 156 Tiles for collection, 4 of them are C_HOUSE
        for (int i = 0; i < 152; i++) {
            Tile tile = tilesCollection.getNew();
            assertNotNull(tile);
        }

        // Now it should return null
        Tile emptyTile = tilesCollection.getNew();
        assertNull(emptyTile);
    }

    @Test
    void testEquals() {
        TilesCollection anotherCollection = new TilesCollection();
        assertFalse(tilesCollection.equals(anotherCollection));
        assertFalse(tilesCollection.equals(null));
        assertTrue(tilesCollection.equals(tilesCollection));
    }

    @Test
    void connectorTypeTest(){
        assertEquals('o', Tile.ConnectorType.ONE_PIPE.toChar());
        assertEquals(Tile.ConnectorType.UNIVERSAL, Tile.ConnectorType.fromChar('u'));
    }

}