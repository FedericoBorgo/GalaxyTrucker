package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {
    @Test
    void testTile(){
        Tile tile = new Tile(TilesType.ROCKET, "suto");

        assertEquals(TilesType.ROCKET, tile.getType());

        Map<Tile.Side, ConnectorType> connectors = tile.getConnectors();
        assertEquals(ConnectorType.SMOOTH, connectors.get(Tile.Side.UP));
        assertEquals(ConnectorType.UNIVERSAL, connectors.get(Tile.Side.RIGHT));
        assertEquals(ConnectorType.TWO_PIPE, connectors.get(Tile.Side.DOWN));
        assertEquals(ConnectorType.ONE_PIPE, connectors.get(Tile.Side.LEFT));
    }
}