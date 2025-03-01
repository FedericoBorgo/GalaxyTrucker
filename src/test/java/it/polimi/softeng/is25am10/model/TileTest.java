package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {
    @Test
    void testTile(){
        Tile tile = new Tile(TilesType.ROCKET, "suto");

        assertEquals(TilesType.ROCKET, tile.getType());

        ConnectorType[] connectors = tile.getConnectorType();
        assertEquals(ConnectorType.SMOOTH, connectors[0] );
        assertEquals(ConnectorType.UNIVERSAL, connectors[1] );
        assertEquals(ConnectorType.TWO_PIPE, connectors[2] );
        assertEquals(ConnectorType.ONE_PIPE, connectors[3] );


    }


}