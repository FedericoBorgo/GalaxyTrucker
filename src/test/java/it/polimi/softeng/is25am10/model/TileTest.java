package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {
    @Test
    void testTile(){
        Tile tile = new Tile(Tile.Type.ROCKET, "suto");

        assertEquals(Tile.Type.ROCKET, tile.getType());

        Map<Tile.Side, Tile.ConnectorType> connectors = tile.getConnectors();
        assertEquals(Tile.ConnectorType.SMOOTH, connectors.get(Tile.Side.UP));
        assertEquals(Tile.ConnectorType.UNIVERSAL, connectors.get(Tile.Side.RIGHT));
        assertEquals(Tile.ConnectorType.TWO_PIPE, connectors.get(Tile.Side.DOWN));
        assertEquals(Tile.ConnectorType.ONE_PIPE, connectors.get(Tile.Side.LEFT));
    }
}